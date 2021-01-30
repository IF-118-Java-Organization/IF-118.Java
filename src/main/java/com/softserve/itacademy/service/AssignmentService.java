package com.softserve.itacademy.service;

import com.softserve.itacademy.entity.Assignment;
import com.softserve.itacademy.request.AssignmentRequest;
import com.softserve.itacademy.response.AssignmentResponse;
import com.softserve.itacademy.response.DownloadFileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AssignmentService {

    AssignmentResponse findById(Integer id);
    List<AssignmentResponse> findAllByOwnerId(Integer id);
    AssignmentResponse create(AssignmentRequest assignmentRequest, MultipartFile file);
    DownloadFileResponse downloadById(Integer id);
    Assignment getById(Integer id);
}
