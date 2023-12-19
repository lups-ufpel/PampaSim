// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: pampaos.proto

// Protobuf Java Version: 3.25.1
package com.pampaos.utils.grpc;

/**
 * Protobuf type {@code Process}
 */
public final class Process extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:Process)
    ProcessOrBuilder {
private static final long serialVersionUID = 0L;
  // Use Process.newBuilder() to construct.
  private Process(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private Process() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new Process();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.pampaos.utils.grpc.PampaOsProto.internal_static_Process_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.pampaos.utils.grpc.PampaOsProto.internal_static_Process_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.pampaos.utils.grpc.Process.class, com.pampaos.utils.grpc.Process.Builder.class);
  }

  public static final int PID_FIELD_NUMBER = 1;
  private int pid_ = 0;
  /**
   * <code>int32 pid = 1;</code>
   * @return The pid.
   */
  @java.lang.Override
  public int getPid() {
    return pid_;
  }

  public static final int STATE_FIELD_NUMBER = 2;
  private int state_ = 0;
  /**
   * <code>int32 state = 2;</code>
   * @return The state.
   */
  @java.lang.Override
  public int getState() {
    return state_;
  }

  public static final int ARRIVALTIME_FIELD_NUMBER = 3;
  private int arrivalTime_ = 0;
  /**
   * <code>int32 arrivalTime = 3;</code>
   * @return The arrivalTime.
   */
  @java.lang.Override
  public int getArrivalTime() {
    return arrivalTime_;
  }

  public static final int BURSTTIME_FIELD_NUMBER = 4;
  private int burstTime_ = 0;
  /**
   * <code>int32 burstTime = 4;</code>
   * @return The burstTime.
   */
  @java.lang.Override
  public int getBurstTime() {
    return burstTime_;
  }

  public static final int PRIORITY_FIELD_NUMBER = 5;
  private int priority_ = 0;
  /**
   * <code>int32 priority = 5;</code>
   * @return The priority.
   */
  @java.lang.Override
  public int getPriority() {
    return priority_;
  }

  public static final int CURREXECTIME_FIELD_NUMBER = 6;
  private int currExecTime_ = 0;
  /**
   * <code>int32 currExecTime = 6;</code>
   * @return The currExecTime.
   */
  @java.lang.Override
  public int getCurrExecTime() {
    return currExecTime_;
  }

  public static final int EXECTIMESLICE_FIELD_NUMBER = 7;
  private int execTimeSlice_ = 0;
  /**
   * <code>int32 execTimeSlice = 7;</code>
   * @return The execTimeSlice.
   */
  @java.lang.Override
  public int getExecTimeSlice() {
    return execTimeSlice_;
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
    if (pid_ != 0) {
      output.writeInt32(1, pid_);
    }
    if (state_ != 0) {
      output.writeInt32(2, state_);
    }
    if (arrivalTime_ != 0) {
      output.writeInt32(3, arrivalTime_);
    }
    if (burstTime_ != 0) {
      output.writeInt32(4, burstTime_);
    }
    if (priority_ != 0) {
      output.writeInt32(5, priority_);
    }
    if (currExecTime_ != 0) {
      output.writeInt32(6, currExecTime_);
    }
    if (execTimeSlice_ != 0) {
      output.writeInt32(7, execTimeSlice_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (pid_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, pid_);
    }
    if (state_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, state_);
    }
    if (arrivalTime_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(3, arrivalTime_);
    }
    if (burstTime_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(4, burstTime_);
    }
    if (priority_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(5, priority_);
    }
    if (currExecTime_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(6, currExecTime_);
    }
    if (execTimeSlice_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(7, execTimeSlice_);
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
    if (!(obj instanceof com.pampaos.utils.grpc.Process)) {
      return super.equals(obj);
    }
    com.pampaos.utils.grpc.Process other = (com.pampaos.utils.grpc.Process) obj;

    if (getPid()
        != other.getPid()) return false;
    if (getState()
        != other.getState()) return false;
    if (getArrivalTime()
        != other.getArrivalTime()) return false;
    if (getBurstTime()
        != other.getBurstTime()) return false;
    if (getPriority()
        != other.getPriority()) return false;
    if (getCurrExecTime()
        != other.getCurrExecTime()) return false;
    if (getExecTimeSlice()
        != other.getExecTimeSlice()) return false;
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
    hash = (37 * hash) + PID_FIELD_NUMBER;
    hash = (53 * hash) + getPid();
    hash = (37 * hash) + STATE_FIELD_NUMBER;
    hash = (53 * hash) + getState();
    hash = (37 * hash) + ARRIVALTIME_FIELD_NUMBER;
    hash = (53 * hash) + getArrivalTime();
    hash = (37 * hash) + BURSTTIME_FIELD_NUMBER;
    hash = (53 * hash) + getBurstTime();
    hash = (37 * hash) + PRIORITY_FIELD_NUMBER;
    hash = (53 * hash) + getPriority();
    hash = (37 * hash) + CURREXECTIME_FIELD_NUMBER;
    hash = (53 * hash) + getCurrExecTime();
    hash = (37 * hash) + EXECTIMESLICE_FIELD_NUMBER;
    hash = (53 * hash) + getExecTimeSlice();
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.pampaos.utils.grpc.Process parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.pampaos.utils.grpc.Process parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.pampaos.utils.grpc.Process parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.pampaos.utils.grpc.Process parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.pampaos.utils.grpc.Process parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.pampaos.utils.grpc.Process parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.pampaos.utils.grpc.Process parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.pampaos.utils.grpc.Process parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public static com.pampaos.utils.grpc.Process parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }

  public static com.pampaos.utils.grpc.Process parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.pampaos.utils.grpc.Process parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.pampaos.utils.grpc.Process parseFrom(
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
  public static Builder newBuilder(com.pampaos.utils.grpc.Process prototype) {
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
   * Protobuf type {@code Process}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:Process)
      com.pampaos.utils.grpc.ProcessOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.pampaos.utils.grpc.PampaOsProto.internal_static_Process_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.pampaos.utils.grpc.PampaOsProto.internal_static_Process_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.pampaos.utils.grpc.Process.class, com.pampaos.utils.grpc.Process.Builder.class);
    }

    // Construct using com.pampaos.utils.grpc.Process.newBuilder()
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
      pid_ = 0;
      state_ = 0;
      arrivalTime_ = 0;
      burstTime_ = 0;
      priority_ = 0;
      currExecTime_ = 0;
      execTimeSlice_ = 0;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.pampaos.utils.grpc.PampaOsProto.internal_static_Process_descriptor;
    }

    @java.lang.Override
    public com.pampaos.utils.grpc.Process getDefaultInstanceForType() {
      return com.pampaos.utils.grpc.Process.getDefaultInstance();
    }

    @java.lang.Override
    public com.pampaos.utils.grpc.Process build() {
      com.pampaos.utils.grpc.Process result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.pampaos.utils.grpc.Process buildPartial() {
      com.pampaos.utils.grpc.Process result = new com.pampaos.utils.grpc.Process(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.pampaos.utils.grpc.Process result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.pid_ = pid_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.state_ = state_;
      }
      if (((from_bitField0_ & 0x00000004) != 0)) {
        result.arrivalTime_ = arrivalTime_;
      }
      if (((from_bitField0_ & 0x00000008) != 0)) {
        result.burstTime_ = burstTime_;
      }
      if (((from_bitField0_ & 0x00000010) != 0)) {
        result.priority_ = priority_;
      }
      if (((from_bitField0_ & 0x00000020) != 0)) {
        result.currExecTime_ = currExecTime_;
      }
      if (((from_bitField0_ & 0x00000040) != 0)) {
        result.execTimeSlice_ = execTimeSlice_;
      }
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
      if (other instanceof com.pampaos.utils.grpc.Process) {
        return mergeFrom((com.pampaos.utils.grpc.Process)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.pampaos.utils.grpc.Process other) {
      if (other == com.pampaos.utils.grpc.Process.getDefaultInstance()) return this;
      if (other.getPid() != 0) {
        setPid(other.getPid());
      }
      if (other.getState() != 0) {
        setState(other.getState());
      }
      if (other.getArrivalTime() != 0) {
        setArrivalTime(other.getArrivalTime());
      }
      if (other.getBurstTime() != 0) {
        setBurstTime(other.getBurstTime());
      }
      if (other.getPriority() != 0) {
        setPriority(other.getPriority());
      }
      if (other.getCurrExecTime() != 0) {
        setCurrExecTime(other.getCurrExecTime());
      }
      if (other.getExecTimeSlice() != 0) {
        setExecTimeSlice(other.getExecTimeSlice());
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
            case 8: {
              pid_ = input.readInt32();
              bitField0_ |= 0x00000001;
              break;
            } // case 8
            case 16: {
              state_ = input.readInt32();
              bitField0_ |= 0x00000002;
              break;
            } // case 16
            case 24: {
              arrivalTime_ = input.readInt32();
              bitField0_ |= 0x00000004;
              break;
            } // case 24
            case 32: {
              burstTime_ = input.readInt32();
              bitField0_ |= 0x00000008;
              break;
            } // case 32
            case 40: {
              priority_ = input.readInt32();
              bitField0_ |= 0x00000010;
              break;
            } // case 40
            case 48: {
              currExecTime_ = input.readInt32();
              bitField0_ |= 0x00000020;
              break;
            } // case 48
            case 56: {
              execTimeSlice_ = input.readInt32();
              bitField0_ |= 0x00000040;
              break;
            } // case 56
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

    private int pid_ ;
    /**
     * <code>int32 pid = 1;</code>
     * @return The pid.
     */
    @java.lang.Override
    public int getPid() {
      return pid_;
    }
    /**
     * <code>int32 pid = 1;</code>
     * @param value The pid to set.
     * @return This builder for chaining.
     */
    public Builder setPid(int value) {

      pid_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>int32 pid = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearPid() {
      bitField0_ = (bitField0_ & ~0x00000001);
      pid_ = 0;
      onChanged();
      return this;
    }

    private int state_ ;
    /**
     * <code>int32 state = 2;</code>
     * @return The state.
     */
    @java.lang.Override
    public int getState() {
      return state_;
    }
    /**
     * <code>int32 state = 2;</code>
     * @param value The state to set.
     * @return This builder for chaining.
     */
    public Builder setState(int value) {

      state_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>int32 state = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearState() {
      bitField0_ = (bitField0_ & ~0x00000002);
      state_ = 0;
      onChanged();
      return this;
    }

    private int arrivalTime_ ;
    /**
     * <code>int32 arrivalTime = 3;</code>
     * @return The arrivalTime.
     */
    @java.lang.Override
    public int getArrivalTime() {
      return arrivalTime_;
    }
    /**
     * <code>int32 arrivalTime = 3;</code>
     * @param value The arrivalTime to set.
     * @return This builder for chaining.
     */
    public Builder setArrivalTime(int value) {

      arrivalTime_ = value;
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <code>int32 arrivalTime = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearArrivalTime() {
      bitField0_ = (bitField0_ & ~0x00000004);
      arrivalTime_ = 0;
      onChanged();
      return this;
    }

    private int burstTime_ ;
    /**
     * <code>int32 burstTime = 4;</code>
     * @return The burstTime.
     */
    @java.lang.Override
    public int getBurstTime() {
      return burstTime_;
    }
    /**
     * <code>int32 burstTime = 4;</code>
     * @param value The burstTime to set.
     * @return This builder for chaining.
     */
    public Builder setBurstTime(int value) {

      burstTime_ = value;
      bitField0_ |= 0x00000008;
      onChanged();
      return this;
    }
    /**
     * <code>int32 burstTime = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearBurstTime() {
      bitField0_ = (bitField0_ & ~0x00000008);
      burstTime_ = 0;
      onChanged();
      return this;
    }

    private int priority_ ;
    /**
     * <code>int32 priority = 5;</code>
     * @return The priority.
     */
    @java.lang.Override
    public int getPriority() {
      return priority_;
    }
    /**
     * <code>int32 priority = 5;</code>
     * @param value The priority to set.
     * @return This builder for chaining.
     */
    public Builder setPriority(int value) {

      priority_ = value;
      bitField0_ |= 0x00000010;
      onChanged();
      return this;
    }
    /**
     * <code>int32 priority = 5;</code>
     * @return This builder for chaining.
     */
    public Builder clearPriority() {
      bitField0_ = (bitField0_ & ~0x00000010);
      priority_ = 0;
      onChanged();
      return this;
    }

    private int currExecTime_ ;
    /**
     * <code>int32 currExecTime = 6;</code>
     * @return The currExecTime.
     */
    @java.lang.Override
    public int getCurrExecTime() {
      return currExecTime_;
    }
    /**
     * <code>int32 currExecTime = 6;</code>
     * @param value The currExecTime to set.
     * @return This builder for chaining.
     */
    public Builder setCurrExecTime(int value) {

      currExecTime_ = value;
      bitField0_ |= 0x00000020;
      onChanged();
      return this;
    }
    /**
     * <code>int32 currExecTime = 6;</code>
     * @return This builder for chaining.
     */
    public Builder clearCurrExecTime() {
      bitField0_ = (bitField0_ & ~0x00000020);
      currExecTime_ = 0;
      onChanged();
      return this;
    }

    private int execTimeSlice_ ;
    /**
     * <code>int32 execTimeSlice = 7;</code>
     * @return The execTimeSlice.
     */
    @java.lang.Override
    public int getExecTimeSlice() {
      return execTimeSlice_;
    }
    /**
     * <code>int32 execTimeSlice = 7;</code>
     * @param value The execTimeSlice to set.
     * @return This builder for chaining.
     */
    public Builder setExecTimeSlice(int value) {

      execTimeSlice_ = value;
      bitField0_ |= 0x00000040;
      onChanged();
      return this;
    }
    /**
     * <code>int32 execTimeSlice = 7;</code>
     * @return This builder for chaining.
     */
    public Builder clearExecTimeSlice() {
      bitField0_ = (bitField0_ & ~0x00000040);
      execTimeSlice_ = 0;
      onChanged();
      return this;
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


    // @@protoc_insertion_point(builder_scope:Process)
  }

  // @@protoc_insertion_point(class_scope:Process)
  private static final com.pampaos.utils.grpc.Process DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.pampaos.utils.grpc.Process();
  }

  public static com.pampaos.utils.grpc.Process getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<Process>
      PARSER = new com.google.protobuf.AbstractParser<Process>() {
    @java.lang.Override
    public Process parsePartialFrom(
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

  public static com.google.protobuf.Parser<Process> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<Process> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.pampaos.utils.grpc.Process getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

