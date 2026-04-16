package com.tujuhsembilan.smartedutelu.domain.media.service;

import com.tujuhsembilan.smartedutelu.common.enums.ErrorCode;
import com.tujuhsembilan.smartedutelu.common.exception.ResourceNotFoundException;
import com.tujuhsembilan.smartedutelu.common.security.SecurityUtils;
import com.tujuhsembilan.smartedutelu.domain.identity.entity.User;
import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserRepository;
import com.tujuhsembilan.smartedutelu.domain.media.dto.request.CreateMediaRequest;
import com.tujuhsembilan.smartedutelu.domain.media.dto.response.MediaResponse;
import com.tujuhsembilan.smartedutelu.domain.media.entity.MediaFile;
import com.tujuhsembilan.smartedutelu.domain.media.repository.MediaFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService {

    private final MediaFileRepository mediaFileRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<MediaResponse> listByOwner(UUID ownerId, Pageable pageable) {
        return mediaFileRepository.findByOwnerId(ownerId, pageable).map(MediaResponse::from);
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
    public MediaResponse registerMedia(CreateMediaRequest request) {
        User currentUser = resolveCurrentUser();

        MediaFile file = MediaFile.builder()
                .owner(currentUser)
                .fileName(request.getFileName())
                .filePath(request.getFilePath())
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
        mediaFileRepository.delete(file);
    }

    private User resolveCurrentUser() {
        String email = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SE_USR_001));
    }
}
