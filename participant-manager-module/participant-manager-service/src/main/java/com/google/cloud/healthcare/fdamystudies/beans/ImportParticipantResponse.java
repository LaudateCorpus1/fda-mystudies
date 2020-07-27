/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class ImportParticipantResponse extends BaseResponse {

  private List<ParticipantDetailRequest> participants = new ArrayList<>();

  private Set<String> invalidEmails = new HashSet<>();

  private Set<String> duplicateEmails = new HashSet<>();

  private List<String> participantIds = new ArrayList<>();

  public ImportParticipantResponse(ErrorCode errorCode) {
    super(errorCode);
  }

  public ImportParticipantResponse(MessageCode messageCode) {
    super(messageCode);
  }
}
