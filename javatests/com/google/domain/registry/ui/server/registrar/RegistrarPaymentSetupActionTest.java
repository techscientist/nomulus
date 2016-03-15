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

package com.google.domain.registry.ui.server.registrar;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import com.google.domain.registry.config.RegistryEnvironment;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenGateway;

import org.joda.money.CurrencyUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/** Tests for {@link RegistrarPaymentSetupAction}. */
@RunWith(MockitoJUnitRunner.class)
public class RegistrarPaymentSetupActionTest {

  @Mock
  private BraintreeGateway braintreeGateway;

  @Mock
  private ClientTokenGateway clientTokenGateway;

  private final RegistrarPaymentSetupAction action = new RegistrarPaymentSetupAction();

  @Before
  public void before() throws Exception {
    action.braintreeGateway = braintreeGateway;
    when(braintreeGateway.clientToken()).thenReturn(clientTokenGateway);
  }

  @Test
  public void testTokenGeneration() throws Exception {
    action.brainframe = "/doodle";
    action.accountIds =
        ImmutableMap.of(
            CurrencyUnit.USD, "sorrow",
            CurrencyUnit.JPY, "torment");
    String blanketsOfSadness = "our hearts are beating, but no one is breathing";
    when(clientTokenGateway.generate()).thenReturn(blanketsOfSadness);
    assertThat(action.handleJsonRequest(ImmutableMap.<String, Object>of()))
        .containsExactly(
            "status", "SUCCESS",
            "message", "Success",
            "results", asList(
                ImmutableMap.of(
                    "token", blanketsOfSadness,
                    "currencies", asList("USD", "JPY"),
                    "brainframe", "/doodle")));
  }

  @Test
  public void testNonEmptyRequestObject_returnsError() throws Exception {
    assertThat(action.handleJsonRequest(ImmutableMap.of("oh", "no")))
        .containsExactly(
            "status", "ERROR",
            "message", "JSON request object must be empty",
            "results", asList());
  }

  @Test
  public void testSandboxEnvironment_returnsError() throws Exception {
    action.environment = RegistryEnvironment.SANDBOX;
    assertThat(action.handleJsonRequest(ImmutableMap.<String, Object>of()))
        .containsExactly(
            "status", "ERROR",
            "message", "sandbox",
            "results", asList());
  }
}