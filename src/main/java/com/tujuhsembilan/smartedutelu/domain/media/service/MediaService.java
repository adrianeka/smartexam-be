package com.tujuhsembilan.smartedutelu.domain.media.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.BusinessException;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.common.security.CurrentUserProvider;
import com.tujuhsembilan.smartedutelu.common.security.SecurityUtils;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.media.dto.request.CreateMediaRequest;
import com.tujuhsembilan.smartedutelu.domain.media.dto.response.MediaResponse;
import com.tujuhsembilan.smartedutelu.domain.media.entity.MediaFile;
import com.tujuhsembilan.smartedutelu.domain.media.repository.MediaFileRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService {

    private final MediaFileRepository mediaFileRepository;
    private final CurrentUserProvider currentUserProvider;
    private final S3Client s3Client;

    @Value("${application.minio.bucket}")
    private String bucket;

    @Value("${application.minio.endpoint}")
    private String endpoint;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/jpg", "image/gif", "image/webp",
            "video/mp4", "video/webm",
            "application/pdf",
            "audio/mpeg", "audio/mp3"
    );

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    // H1: Magic byte signatures for content-type validation
    private static final Map<String, byte[]> MAGIC_BYTES = Map.of(
            "image/jpeg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF},
            "image/jpg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF},
            "image/png", new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47},
            "image/gif", new byte[]{0x47, 0x49, 0x46},
            "application/pdf", new byte[]{0x25, 0x50, 0x44, 0x46}
    );

    // H2: Ownership check — force ownerId to currentUser for non-ADMIN
    @Transactional(readOnly = true)
    public Page<MediaResponse> listByOwner(UUID ownerId, Pageable pageable) {
        UUID effectiveOwnerId = ownerId;
        if (!SecurityUtils.hasCurrentRole("ADMIN")) {
            User currentUser = currentUserProvider.getCurrentUser();
            effectiveOwnerId = currentUser.getId();
        }
        return mediaFileRepository.findByOwnerId(effectiveOwnerId, pageable).map(MediaResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<MediaResponse> listRecentByCurrentUser(Pageable pageable) {
        User user = currentUserProvider.getCurrentUser();
        return mediaFileRepository.findByOwnerIdOrderByUploadedAtDesc(user.getId(), pageable)
                .map(MediaResponse::from);
    }

    @Transactional(readOnly = true)
    public List<MediaResponse> listByContext(String context, UUID contextId) {
        return mediaFileRepository.findByContextAndContextId(context, contextId)
                .stream().map(MediaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public MediaResponse getMedia(UUID id) {
        MediaFile file = mediaFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_MDA_001));
        return MediaResponse.from(file);
    }

    @Transactional
    @CircuitBreaker(name = "s3", fallbackMethod = "uploadMediaFallback")
    public MediaResponse uploadMedia(MultipartFile file, String context, UUID contextId) {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.SE_CMN_001);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.SE_CMN_001);
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException(ErrorCode.SE_CMN_001);
        }

        // H1: Validate magic bytes to prevent content-type spoofing
        validateMagicBytes(file, contentType);

        User currentUser = currentUserProvider.getCurrentUser();

        String extension = getExtension(file.getOriginalFilename());
        String key = "media/" + UUID.randomUUID() + extension;

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(contentType)
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            log.error("Failed to upload file to S3", e);
            throw new BusinessException(ErrorCode.SE_CMN_007);
        }

        String fileUrl = endpoint + "/" + bucket + "/" + key;

        MediaFile mediaFile = MediaFile.builder()
                .owner(currentUser)
                .fileName(file.getOriginalFilename())
                .filePath(fileUrl)
                .fileType(contentType)
                .fileSize(file.getSize())   // J16: Long to support large files
                .context(context)
                .contextId(contextId)
                .build();

        return MediaResponse.from(mediaFileRepository.save(mediaFile));
    }

    @Transactional
    public MediaResponse registerMedia(CreateMediaRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();

        // H6: Validate filePath to prevent path traversal
        String filePath = request.getFilePath();
        if (filePath.contains("..") || filePath.contains("\\")) {
            throw new BusinessException(ErrorCode.SE_CMN_006, "File path tidak valid");
        }
        String expectedPrefix = endpoint + "/" + bucket + "/";
        if (!filePath.startsWith(expectedPrefix) && !filePath.startsWith("media/")) {
            throw new BusinessException(ErrorCode.SE_CMN_006, "File path harus menggunakan prefix yang valid");
        }

        MediaFile file = MediaFile.builder()
                .owner(currentUser)
                .fileName(request.getFileName())
                .filePath(filePath)
                .fileType(request.getFileType())
                .fileSize(request.getFileSize())
                .context(request.getContext())
                .contextId(request.getContextId())
                .build();

        return MediaResponse.from(mediaFileRepository.save(file));
    }

    @Transactional
    public void deleteMedia(UUID id) {
        MediaFile file = mediaFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_MDA_001));

        // Try to delete from S3 if the path matches our bucket
        String prefix = endpoint + "/" + bucket + "/";
        if (file.getFilePath() != null && file.getFilePath().startsWith(prefix)) {
            String key = file.getFilePath().substring(prefix.length());
            try {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucket).key(key).build());
            } catch (Exception e) {
                log.warn("Failed to delete S3 object: {}", key, e);
            }
        }

        mediaFileRepository.delete(file);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.'));
    }

    private void validateMagicBytes(MultipartFile file, String contentType) {
        byte[] expected = MAGIC_BYTES.get(contentType.toLowerCase());
        if (expected == null) return; // No magic bytes check for video/audio types

        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[expected.length];
            int read = is.read(header);
            if (read < expected.length) {
                throw new BusinessException(ErrorCode.SE_CMN_001, "File terlalu kecil atau kosong");
            }
            for (int i = 0; i < expected.length; i++) {
                if (header[i] != expected[i]) {
                    throw new BusinessException(ErrorCode.SE_CMN_001,
                            "Content-type tidak sesuai dengan isi file");
                }
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SE_CMN_007, "Gagal membaca file");
        }
    }

    // J8: Circuit breaker fallback — dipanggil saat S3 tidak dapat dijangkau
    private MediaResponse uploadMediaFallback(MultipartFile file, String context, UUID contextId, Throwable t) {
        log.error("Circuit breaker aktif: S3 tidak tersedia. Upload gagal untuk file '{}'. Penyebab: {}",
                file.getOriginalFilename(), t.getMessage());
        throw new BusinessException(ErrorCode.SE_CMN_007, "Layanan penyimpanan sementara tidak tersedia, coba lagi nanti");
    }

}
