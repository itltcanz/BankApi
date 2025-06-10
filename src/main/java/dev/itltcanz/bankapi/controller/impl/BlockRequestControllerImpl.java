package dev.itltcanz.bankapi.controller.impl;

import dev.itltcanz.bankapi.controller.BlockRequestController;
import dev.itltcanz.bankapi.dto.request.BlockRequestDtoCreate;
import dev.itltcanz.bankapi.dto.request.BlockRequestDtoResponse;
import dev.itltcanz.bankapi.service.impl.BlockRequestServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BlockRequestControllerImpl implements BlockRequestController {

  private final BlockRequestServiceImpl blockRequestService;

  public ResponseEntity<BlockRequestDtoResponse> createRequest(BlockRequestDtoCreate dto) {
    var response = blockRequestService.createRequest(dto);
    return ResponseEntity.ok(response);
  }

  public ResponseEntity<Page<BlockRequestDtoResponse>> getRequestsAdmin(int page, int size,
      String sortBy, String direction) {
    var sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
    var pageable = PageRequest.of(page, size, sort);
    var response = blockRequestService.getRequestsAdmin(pageable);
    return ResponseEntity.ok(response);
  }

  public ResponseEntity<Page<BlockRequestDtoResponse>> getRequestsUser(int page, int size,
      String sortBy, String direction) {
    var sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
    var pageable = PageRequest.of(page, size, sort);
    var response = blockRequestService.getRequestsUser(pageable);
    return ResponseEntity.ok(response);
  }

  public ResponseEntity<BlockRequestDtoResponse> getRequest(String requestId) {
    var response = blockRequestService.getRequest(requestId);
    return ResponseEntity.ok(response);
  }

  public ResponseEntity<BlockRequestDtoResponse> approveRequest(String requestId) {
    var response = blockRequestService.approveRequest(requestId);
    return ResponseEntity.ok(response);
  }

  public ResponseEntity<BlockRequestDtoResponse> rejectRequest(String requestId) {
    var response = blockRequestService.rejectRequest(requestId);
    return ResponseEntity.ok(response);
  }
}