package com.banksystem.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.banksystem.model.Submission;

@Slf4j
@Component
public class AuditSystem {

    public void submit(Submission submission) {
        log.info(submission.toString());
    }
}
