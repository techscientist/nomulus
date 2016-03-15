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

package com.google.domain.registry.model.host;

import static com.google.common.collect.Sets.intersection;
import static com.google.domain.registry.util.CollectionUtils.nullSafeImmutableCopy;
import static com.google.domain.registry.util.CollectionUtils.nullToEmptyImmutableCopy;

import com.google.common.collect.ImmutableSet;
import com.google.domain.registry.model.eppinput.ResourceCommand.AbstractSingleResourceCommand;
import com.google.domain.registry.model.eppinput.ResourceCommand.ResourceCheck;
import com.google.domain.registry.model.eppinput.ResourceCommand.ResourceCreateOrChange;
import com.google.domain.registry.model.eppinput.ResourceCommand.ResourceUpdate;

import java.net.InetAddress;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/** A collection of {@link HostResource} commands. */
public class HostCommand {

  /** The fields on "chgType" from {@link "http://tools.ietf.org/html/rfc5732"}. */
  @XmlTransient
  abstract static class HostCreateOrChange extends AbstractSingleResourceCommand
      implements ResourceCreateOrChange<HostResource.Builder> {
    /** IP Addresses for this host. Can be null if this is an external host. */
    @XmlElement(name = "addr")
    Set<InetAddress> inetAddresses;

    public ImmutableSet<InetAddress> getInetAddresses() {
      return nullSafeImmutableCopy(inetAddresses);
    }

    public String getFullyQualifiedHostName() {
      return getTargetId();
    }

    @Override
    public void applyTo(HostResource.Builder builder) {
      if (getFullyQualifiedHostName() != null) {
        builder.setFullyQualifiedHostName(getFullyQualifiedHostName());
      }
      if (getInetAddresses() != null) {
        builder.setInetAddresses(getInetAddresses());
      }
    }
  }

  /**
   * A create command for a {@link HostResource}, mapping "createType" from
   * {@link "http://tools.ietf.org/html/rfc5732"}.
   */
  @XmlType(propOrder = {"targetId", "inetAddresses" })
  @XmlRootElement
  public static class Create
      extends HostCreateOrChange implements ResourceCreateOrChange<HostResource.Builder> {}

  /** A delete command for a {@link HostResource}. */
  @XmlRootElement
  public static class Delete extends AbstractSingleResourceCommand {}

  /** An info request for a {@link HostResource}. */
  @XmlRootElement
  public static class Info extends AbstractSingleResourceCommand {}

  /** A check request for {@link HostResource}. */
  @XmlRootElement
  public static class Check extends ResourceCheck {}

  /** An update to a {@link HostResource}. */
  @XmlRootElement
  @XmlType(propOrder = {"targetId", "innerAdd", "innerRemove", "innerChange"})
  public static class Update extends ResourceUpdate
      <Update.AddRemove, HostResource.Builder, Update.Change> {

    @XmlElement(name = "chg")
    protected Change innerChange;

    @XmlElement(name = "add")
    protected AddRemove innerAdd;

    @XmlElement(name = "rem")
    protected AddRemove innerRemove;

    @Override
    protected Change getNullableInnerChange() {
      return innerChange;
    }

    @Override
    protected AddRemove getNullableInnerAdd() {
      return innerAdd;
    }

    @Override
    protected AddRemove getNullableInnerRemove() {
      return innerRemove;
    }

    /** The add/remove type on a host update command. */
    @XmlType(propOrder = { "inetAddresses", "statusValues" })
    public static class AddRemove extends ResourceUpdate.AddRemove {
      /** IP Addresses for this host. Can be null if this is an external host. */
      @XmlElement(name = "addr")
      Set<InetAddress> inetAddresses;

      public ImmutableSet<InetAddress> getInetAddresses() {
        return nullToEmptyImmutableCopy(inetAddresses);
      }
    }

    /** The inner change type on a host update command. */
    @XmlType(propOrder = {"targetId", "inetAddresses" })
    public static class Change extends HostCreateOrChange {}

    @Override
    public void applyTo(HostResource.Builder builder) throws AddRemoveSameValueException {
      super.applyTo(builder);
      if (!intersection(getInnerAdd().getInetAddresses(), getInnerRemove().getInetAddresses())
          .isEmpty()) {
        throw new AddRemoveSameValueException();
      }
      builder.addInetAddresses(getInnerAdd().getInetAddresses());
      builder.removeInetAddresses(getInnerRemove().getInetAddresses());
    }
  }
}