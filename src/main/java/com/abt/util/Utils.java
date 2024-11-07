package com.abt.util;

import com.abt.domain.LtfClientRequest;
import com.abt.domain.Task;
import org.joda.time.DateTime;

import java.util.UUID;

public class Utils {

    public static String concatenateFullName(String firstName, String middleName, String surname) {
        // Use a StringBuilder for efficient string concatenation
        StringBuilder fullName = new StringBuilder();

        // Append each part if it's not null or blank
        if (firstName != null && !firstName.isBlank()) {
            fullName.append(firstName.trim());
        }

        if (middleName != null && !middleName.isBlank()) {
            if (!fullName.isEmpty()) fullName.append(" ");
            fullName.append(middleName.trim());
        }

        if (surname != null && !surname.isBlank()) {
            if (!fullName.isEmpty()) fullName.append(" ");
            fullName.append(surname.trim());
        }

        return fullName.toString();
    }

    public static Task generateTask(LtfClientRequest request, String reasonReference) {
        Task task = new Task();
        DateTime now = new DateTime();
        task.setIdentifier(UUID.randomUUID().toString());
        task.setPlanIdentifier("5270285b-5a3b-4647-b772-c0b3c52e2b71");
        task.setGroupIdentifier(request.getLocationId());
        task.setStatus(Task.TaskStatus.READY);
        task.setBusinessStatus("Referred");
        task.setPriority(3);
        task.setCode("Referral");
        task.setReasonReference(reasonReference);

        task.setDescription("CTC");

        task.setFocus("LTFU");
        task.setForEntity(request.getBaseEntityId());
        task.setExecutionStartDate(now);
        task.setAuthoredOn(now);
        task.setLastModified(now);
        task.setOwner(request.getProviderId());
        task.setRequester(request.getProviderId());
        task.setLocation(null);
        return task;
    }
}
