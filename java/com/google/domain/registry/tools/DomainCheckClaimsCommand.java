// Copyright 2016 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.domain.registry.tools;

import com.google.common.collect.Multimap;
import com.google.domain.registry.tools.Command.GtechCommand;
import com.google.domain.registry.tools.soy.DomainCheckClaimsSoyInfo;
import com.google.template.soy.data.SoyMapData;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.Collection;
import java.util.List;

/** A command to execute a domain check claims epp command. */
@Parameters(separators = " =", commandDescription = "Check claims on domain(s)")
final class DomainCheckClaimsCommand extends EppToolCommand implements GtechCommand {

  @Parameter(
      names = {"-c", "--client"},
      description = "Client identifier of the registrar to execute the command as",
      required = true)
  String clientIdentifier;

  @Parameter(
      description = "Domain(s) to check.",
      required = true)
  private List<String> mainParameters;

  @Override
  void initEppToolCommand() {
    Multimap<String, String> domainNameMap = validateAndGroupDomainNamesByTld(mainParameters);
    for (Collection<String> values : domainNameMap.asMap().values()) {
      setSoyTemplate(
          DomainCheckClaimsSoyInfo.getInstance(), DomainCheckClaimsSoyInfo.DOMAINCHECKCLAIMS);
      addSoyRecord(clientIdentifier, new SoyMapData("domainNames", values));
    }
  }
}