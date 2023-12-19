// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: pampaos.proto

// Protobuf Java Version: 3.25.1
package com.pampaos.utils.grpc;

/**
 * Protobuf type {@code ProcessList}
 */
public final class ProcessList extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:ProcessList)
    ProcessListOrBuilder {
private static final long serialVersionUID = 0L;
  // Use ProcessList.newBuilder() to construct.
  private ProcessList(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ProcessList() {
    process_ = java.util.Collections.emptyList();
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new ProcessList();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.pampaos.utils.grpc.PampaOsProto.internal_static_ProcessList_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.pampaos.utils.grpc.PampaOsProto.internal_static_ProcessList_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.pampaos.utils.grpc.ProcessList.class, com.pampaos.utils.grpc.ProcessList.Builder.class);
  }

  public static final int PROCESS_FIELD_NUMBER = 1;
  @SuppressWarnings("serial")
  private java.util.List<com.pampaos.utils.grpc.Process> process_;
  /**
   * <code>repeated .Process process = 1;</code>
   */
  @java.lang.Override
  public java.util.List<com.pampaos.utils.grpc.Process> getProcessList() {
    return process_;
  }
  /**
   * <code>repeated .Process process = 1;</code>
   */
  @java.lang.Override
  public java.util.List<? extends com.pampaos.utils.grpc.ProcessOrBuilder> 
      getProcessOrBuilderList() {
    return process_;
  }
  /**
   * <code>repeated .Process process = 1;</code>
   */
  @java.lang.Override
  public int getProcessCount() {
    return process_.size();
  }
  /**
   * <code>repeated .Process process = 1;</code>
   */
  @java.lang.Override
  public com.pampaos.utils.grpc.Process getProcess(int index) {
    return process_.get(index);
  }
  /**
   * <code>repeated .Process process = 1;</code>
   */
  @java.lang.Override
  public com.pampaos.utils.grpc.ProcessOrBuilder getProcessOrBuilder(
      int index) {
    return process_.get(index);
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    for (int i = 0; i < process_.size(); i++) {
      output.writeMessage(1, process_.get(i));
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    for (int i = 0; i < process_.size(); i++) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, process_.get(i));
    }
    size += getUnknownFields().getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.pampaos.utils.grpc.ProcessList)) {
      return super.equals(obj);
    }
    com.pampaos.utils.grpc.ProcessList other = (com.pampaos.utils.grpc.ProcessList) obj;

    if (!getProcessList()
        .equals(other.getProcessList())) return false;
    if (!getUnknownFields().equals(other.getUnknownFields())) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (getProcessCount() > 0) {
      hash = (37 * hash) + PROCESS_FIELD_NUMBER;
      hash = (53 * hash) + getProcessList().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.pampaos.utils.grpc.ProcessList parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.pampaos.utils.grpc.ProcessList parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.pampaos.utils.grpc.ProcessList parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.pampaos.utils.grpc.ProcessList parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.pampaos.utils.grpc.ProcessList parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.pampaos.utils.grpc.ProcessList parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.pampaos.utils.grpc.ProcessList parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.pampaos.utils.grpc.ProcessList parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public static com.pampaos.utils.grpc.ProcessList parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }

  public static com.pampaos.utils.grpc.ProcessList parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.pampaos.utils.grpc.ProcessList parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.pampaos.utils.grpc.ProcessList parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.pampaos.utils.grpc.ProcessList prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code ProcessList}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:ProcessList)
      com.pampaos.utils.grpc.ProcessListOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.pampaos.utils.grpc.PampaOsProto.internal_static_ProcessList_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.pampaos.utils.grpc.PampaOsProto.internal_static_ProcessList_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.pampaos.utils.grpc.ProcessList.class, com.pampaos.utils.grpc.ProcessList.Builder.class);
    }

    // Construct using com.pampaos.utils.grpc.ProcessList.newBuilder()
    private Builder() {

    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);

    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      if (processBuilder_ == null) {
        process_ = java.util.Collections.emptyList();
      } else {
        process_ = null;
        processBuilder_.clear();
      }
      bitField0_ = (bitField0_ & ~0x00000001);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.pampaos.utils.grpc.PampaOsProto.internal_static_ProcessList_descriptor;
    }

    @java.lang.Override
    public com.pampaos.utils.grpc.ProcessList getDefaultInstanceForType() {
      return com.pampaos.utils.grpc.ProcessList.getDefaultInstance();
    }

    @java.lang.Override
    public com.pampaos.utils.grpc.ProcessList build() {
      com.pampaos.utils.grpc.ProcessList result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.pampaos.utils.grpc.ProcessList buildPartial() {
      com.pampaos.utils.grpc.ProcessList result = new com.pampaos.utils.grpc.ProcessList(this);
      buildPartialRepeatedFields(result);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartialRepeatedFields(com.pampaos.utils.grpc.ProcessList result) {
      if (processBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0)) {
          process_ = java.util.Collections.unmodifiableList(process_);
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.process_ = process_;
      } else {
        result.process_ = processBuilder_.build();
      }
    }

    private void buildPartial0(com.pampaos.utils.grpc.ProcessList result) {
      int from_bitField0_ = bitField0_;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.pampaos.utils.grpc.ProcessList) {
        return mergeFrom((com.pampaos.utils.grpc.ProcessList)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.pampaos.utils.grpc.ProcessList other) {
      if (other == com.pampaos.utils.grpc.ProcessList.getDefaultInstance()) return this;
      if (processBuilder_ == null) {
        if (!other.process_.isEmpty()) {
          if (process_.isEmpty()) {
            process_ = other.process_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureProcessIsMutable();
            process_.addAll(other.process_);
          }
          onChanged();
        }
      } else {
        if (!other.process_.isEmpty()) {
          if (processBuilder_.isEmpty()) {
            processBuilder_.dispose();
            processBuilder_ = null;
            process_ = other.process_;
            bitField0_ = (bitField0_ & ~0x00000001);
            processBuilder_ = 
              com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                 getProcessFieldBuilder() : null;
          } else {
            processBuilder_.addAllMessages(other.process_);
          }
        }
      }
      this.mergeUnknownFields(other.getUnknownFields());
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              com.pampaos.utils.grpc.Process m =
                  input.readMessage(
                      com.pampaos.utils.grpc.Process.parser(),
                      extensionRegistry);
              if (processBuilder_ == null) {
                ensureProcessIsMutable();
                process_.add(m);
              } else {
                processBuilder_.addMessage(m);
              }
              break;
            } // case 10
            default: {
              if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                done = true; // was an endgroup tag
              }
              break;
            } // default:
          } // switch (tag)
        } // while (!done)
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.unwrapIOException();
      } finally {
        onChanged();
      } // finally
      return this;
    }
    private int bitField0_;

    private java.util.List<com.pampaos.utils.grpc.Process> process_ =
      java.util.Collections.emptyList();
    private void ensureProcessIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        process_ = new java.util.ArrayList<com.pampaos.utils.grpc.Process>(process_);
        bitField0_ |= 0x00000001;
       }
    }

    private com.google.protobuf.RepeatedFieldBuilderV3<
        com.pampaos.utils.grpc.Process, com.pampaos.utils.grpc.Process.Builder, com.pampaos.utils.grpc.ProcessOrBuilder> processBuilder_;

    /**
     * <code>repeated .Process process = 1;</code>
     */
    public java.util.List<com.pampaos.utils.grpc.Process> getProcessList() {
      if (processBuilder_ == null) {
        return java.util.Collections.unmodifiableList(process_);
      } else {
        return processBuilder_.getMessageList();
      }
    }
    /**
     * <code>repeated .Process process = 1;</code>
     */
    public int getProcessCount() {
      if (processBuilder_ == null) {
        return process_.size();
      } else {
        return processBuilder_.getCount();
      }
    }
    /**
     * <code>repeated .Process process = 1;</code>
     */
    public com.pampaos.utils.grpc.Process getProcess(int index) {
      if (processBuilder_ == null) {
        return process_.get(index);
      } else {
        return processBuilder_.getMessage(index);
      }
    }
    /**
     * <code>repeated .Process process = 1;</code>
     */
    public Builder setProcess(
        int index, com.pampaos.utils.grpc.Process value) {
      if (processBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureProcessIsMutable();
        process_.set(index, value);
        onChanged();
      } else {
        processBuilder_.setMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .Process process = 1;</code>
     */
    public Builder setProcess(
        int index, com.pampaos.utils.grpc.Process.Builder builderForValue) {
      if (processBuilder_ == null) {
        ensureProcessIsMutable();
        process_.set(index, builderForValue.build());
        onChanged();
      } else {
        processBuilder_.setMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .Process process = 1;</code>
     */
    public Builder addProcess(com.pampaos.utils.grpc.Process value) {
      if (processBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureProcessIsMutable();
        process_.add(value);
        onChanged();
      } else {
        processBuilder_.addMessage(value);
      }
      return this;
    }
    /**
     * <code>repeated .Process process = 1;</code>
     */
    public Builder addProcess(
        int index, com.pampaos.utils.grpc.Process value) {
      if (processBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        ensureProcessIsMutable();
        process_.add(index, value);
        onChanged();
      } else {
        processBuilder_.addMessage(index, value);
      }
      return this;
    }
    /**
     * <code>repeated .Process process = 1;</code>
     */
    public Builder addProcess(
        com.pampaos.utils.grpc.Process.Builder builderForValue) {
      if (processBuilder_ == null) {
        ensureProcessIsMutable();
        process_.add(builderForValue.build());
        onChanged();
      } else {
        processBuilder_.addMessage(builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .Process process = 1;</code>
     */
    public Builder addProcess(
        int index, com.pampaos.utils.grpc.Process.Builder builderForValue) {
      if (processBuilder_ == null) {
        ensureProcessIsMutable();
        process_.add(index, builderForValue.build());
        onChanged();
      } else {
        processBuilder_.addMessage(index, builderForValue.build());
      }
      return this;
    }
    /**
     * <code>repeated .Process process = 1;</code>
     */
    public Builder addAllProcess(
        java.lang.Iterable<? extends com.pampaos.utils.grpc.Process> values) {
      if (processBuilder_ == null) {
        ensureProcessIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, process_);
        onChanged();
      } else {
        processBuilder_.addAllMessages(values);
      }
      return this;
    }
    /**
     * <code>repeated .Process process = 1;</code>
     */
    public Builder clearProcess() {
      if (processBuilder_ == null) {
        process_ = java.util.Collections.emptyList();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
      } else {
        processBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>repeated .Process process = 1;</code>
     */
    public Builder removeProcess(int index) {
      if (processBuilder_ == null) {
        ensureProcessIsMutable();
        process_.remove(index);
        onChanged();
      } else {
        processBuilder_.remove(index);
      }
      return this;
    }
    /**
     * <code>repeated .Process process = 1;</code>
     */
    public com.pampaos.utils.grpc.Process.Builder getProcessBuilder(
        int index) {
      return getProcessFieldBuilder().getBuilder(index);
    }
    /**
     * <code>repeated .Process process = 1;</code>
     */
    public com.pampaos.utils.grpc.ProcessOrBuilder getProcessOrBuilder(
        int index) {
      if (processBuilder_ == null) {
        return process_.get(index);  } else {
        return processBuilder_.getMessageOrBuilder(index);
      }
    }
    /**
     * <code>repeated .Process process = 1;</code>
     */
    public java.util.List<? extends com.pampaos.utils.grpc.ProcessOrBuilder> 
         getProcessOrBuilderList() {
      if (processBuilder_ != null) {
        return processBuilder_.getMessageOrBuilderList();
      } else {
        return java.util.Collections.unmodifiableList(process_);
      }
    }
    /**
     * <code>repeated .Process process = 1;</code>
     */
    public com.pampaos.utils.grpc.Process.Builder addProcessBuilder() {
      return getProcessFieldBuilder().addBuilder(
          com.pampaos.utils.grpc.Process.getDefaultInstance());
    }
    /**
     * <code>repeated .Process process = 1;</code>
     */
    public com.pampaos.utils.grpc.Process.Builder addProcessBuilder(
        int index) {
      return getProcessFieldBuilder().addBuilder(
          index, com.pampaos.utils.grpc.Process.getDefaultInstance());
    }
    /**
     * <code>repeated .Process process = 1;</code>
     */
    public java.util.List<com.pampaos.utils.grpc.Process.Builder> 
         getProcessBuilderList() {
      return getProcessFieldBuilder().getBuilderList();
    }
    private com.google.protobuf.RepeatedFieldBuilderV3<
        com.pampaos.utils.grpc.Process, com.pampaos.utils.grpc.Process.Builder, com.pampaos.utils.grpc.ProcessOrBuilder> 
        getProcessFieldBuilder() {
      if (processBuilder_ == null) {
        processBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
            com.pampaos.utils.grpc.Process, com.pampaos.utils.grpc.Process.Builder, com.pampaos.utils.grpc.ProcessOrBuilder>(
                process_,
                ((bitField0_ & 0x00000001) != 0),
                getParentForChildren(),
                isClean());
        process_ = null;
      }
      return processBuilder_;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:ProcessList)
  }

  // @@protoc_insertion_point(class_scope:ProcessList)
  private static final com.pampaos.utils.grpc.ProcessList DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.pampaos.utils.grpc.ProcessList();
  }

  public static com.pampaos.utils.grpc.ProcessList getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ProcessList>
      PARSER = new com.google.protobuf.AbstractParser<ProcessList>() {
    @java.lang.Override
    public ProcessList parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      Builder builder = newBuilder();
      try {
        builder.mergeFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(builder.buildPartial());
      } catch (com.google.protobuf.UninitializedMessageException e) {
        throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(e)
            .setUnfinishedMessage(builder.buildPartial());
      }
      return builder.buildPartial();
    }
  };

  public static com.google.protobuf.Parser<ProcessList> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ProcessList> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.pampaos.utils.grpc.ProcessList getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

