package com.softserve.itacademy.service.implementation;

import com.softserve.itacademy.entity.Course;
import com.softserve.itacademy.entity.Group;
import com.softserve.itacademy.entity.Invitation;
import com.softserve.itacademy.entity.User;
import com.softserve.itacademy.exception.NotFoundException;
import com.softserve.itacademy.repository.GroupRepository;
import com.softserve.itacademy.repository.InvitationRepository;
import com.softserve.itacademy.repository.UserRepository;
import com.softserve.itacademy.request.InvitationRequest;
import com.softserve.itacademy.response.InvitationResponse;
import com.softserve.itacademy.service.InvitationService;
import com.softserve.itacademy.service.MailSender;
import com.softserve.itacademy.service.converters.InvitationConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InvitationServiceImpl implements InvitationService {
    private final MailSender mailSender;
    private final InvitationConverter invitationConverter;
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    private static final String USER_ID_NOT_FOUND = "User with such id was not found";

    public InvitationServiceImpl(MailSender mailSender, InvitationConverter invitationConverter,
                                 InvitationRepository invitationRepository, UserRepository userRepository,
                                 GroupRepository groupRepository) {
        this.mailSender = mailSender;
        this.invitationConverter = invitationConverter;
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public InvitationResponse sendInvitation(InvitationRequest invitationRequest) {
        log.info("sending invitation on email");
        Invitation invitation = invitationConverter.of(invitationRequest);
        sendInvitationMail(invitation);
        invitation.setLink(getLink(invitation));
        return invitationConverter.of(invitationRepository.save(invitation));
    }

    @Transactional
    @Override
    public void approveByLink(String email, String code) {
        Invitation invitation = getInvitationByCode(code);
        log.info("approving invitation");
        userRepository.updateInvite(code, email);
        approveCourseOrGroup(invitation);
    }

    @Override
    public InvitationResponse findByCode(String code) {
        return invitationConverter.of(getInvitationByCode(code));
    }

    @Override
    public void delete(Integer id) {
        log.info("delete invitation");
        invitationRepository.delete(getById(id));
    }

    @Transactional
    @Override
    public void approveById(Integer id) {
        log.info("approving invitation " + id);
        InvitationResponse invitationResponse = approveCourseOrGroup(getById(id));
        invitationRepository.approve(id, invitationResponse.getCode());
        invitationRepository.deleteById(invitationResponse.getId());
    }

    @Override
    public List<InvitationResponse> findAllByEmail(String email) {
        log.info("looking for all invitations of the user");
        return invitationRepository.findAllByEmail(email).stream()
                .filter(invitation -> !invitation.getApproved())
                .map(invitationConverter::of)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public int deleteByExpirationDate() {
        return invitationRepository.deleteByExpirationDate();
    }

    private InvitationResponse approveCourseOrGroup(Invitation invitation) {
        if (!canBeApproved(invitation)) {
            return InvitationResponse.builder()
                    .approved(false)
                    .build();
        }
        else {
            if (invitation.getGroup() != null) {
                approveGroup(invitation);
            } else {
                approveCourse(invitation);
            }
            return invitationConverter.of(getInvitationByCode(invitation.getCode()));
        }

    }

    private void approveCourse(Invitation invitation) {
        approve(invitation);
        groupRepository.save(getGroup(invitation));
    }

    private void approveGroup(Invitation invitation) {
        approve(invitation);
        invitationRepository.groupApprove(invitation.getUser().getId(), invitation.getGroup().getId());
    }

    private Group getGroup(Invitation invitation) {
        Set<Course> courses = new HashSet<>();
        courses.add(invitation.getCourse());
        Set<User> users = new HashSet<>();
        users.add(invitation.getUser());
        return Group.builder()
                .name(invitation.getCourse().getName() +"-" + invitation.getUser().getName())
                .ownerId(invitation.getOwnerId())
                .courses(courses)
                .users(users)
                .disabled(false)
                .build();
    }

    private Invitation getInvitationByCode(String code) {
        return invitationRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Invitation with such code was not found"));
    }

    private boolean canBeApproved(Invitation invitation) {
        return !invitation.getApproved()
                && invitation.getExpirationDate().isAfter(LocalDateTime.now());
    }

    private void approve(Invitation invitation) {
        invitationRepository.approve(invitation.getUser().getId(), invitation.getCode());
    }

    private void sendInvitationMail(Invitation invitation) {
        User user = userRepository.findById(invitation.getUser().getId())
                .orElseThrow(() -> new NotFoundException(USER_ID_NOT_FOUND));
        mailSender.send(user.getEmail(), "SoftClass invitation",
                getInviteMessage(invitation));
    }

    private String getInviteMessage(Invitation invitation) {
        String inviteTo = invitation.getGroup() != null ? invitation.getGroup().getName() : invitation.getCourse().getName();
        return String.format("You are invited to %s %s", inviteTo, getLink(invitation));
    }

    private String getLink(Invitation invitation) {
        return invitation.getLink() + invitation.getEmail() + "/" + invitation.getCode();
    }

    private Invitation getById(Integer id) {
        return invitationRepository.findById(id).orElseThrow(() -> new NotFoundException("Invitation with such id was not found"));
    }
}

