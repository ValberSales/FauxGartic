package org.fauxgartic.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * MUDANÇA: Nome do serviço atualizado para FauxGarticService
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: fauxgartic.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class FauxGarticServiceGrpc {

  private FauxGarticServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "FauxGarticService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.fauxgartic.grpc.Jogador,
      org.fauxgartic.grpc.EstadoDoJogo> getEntrarNoJogoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "EntrarNoJogo",
      requestType = org.fauxgartic.grpc.Jogador.class,
      responseType = org.fauxgartic.grpc.EstadoDoJogo.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.fauxgartic.grpc.Jogador,
      org.fauxgartic.grpc.EstadoDoJogo> getEntrarNoJogoMethod() {
    io.grpc.MethodDescriptor<org.fauxgartic.grpc.Jogador, org.fauxgartic.grpc.EstadoDoJogo> getEntrarNoJogoMethod;
    if ((getEntrarNoJogoMethod = FauxGarticServiceGrpc.getEntrarNoJogoMethod) == null) {
      synchronized (FauxGarticServiceGrpc.class) {
        if ((getEntrarNoJogoMethod = FauxGarticServiceGrpc.getEntrarNoJogoMethod) == null) {
          FauxGarticServiceGrpc.getEntrarNoJogoMethod = getEntrarNoJogoMethod =
              io.grpc.MethodDescriptor.<org.fauxgartic.grpc.Jogador, org.fauxgartic.grpc.EstadoDoJogo>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "EntrarNoJogo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.fauxgartic.grpc.Jogador.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.fauxgartic.grpc.EstadoDoJogo.getDefaultInstance()))
              .setSchemaDescriptor(new FauxGarticServiceMethodDescriptorSupplier("EntrarNoJogo"))
              .build();
        }
      }
    }
    return getEntrarNoJogoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.fauxgartic.grpc.Jogador,
      org.fauxgartic.grpc.EventoDeJogo> getReceberEventosMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReceberEventos",
      requestType = org.fauxgartic.grpc.Jogador.class,
      responseType = org.fauxgartic.grpc.EventoDeJogo.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.fauxgartic.grpc.Jogador,
      org.fauxgartic.grpc.EventoDeJogo> getReceberEventosMethod() {
    io.grpc.MethodDescriptor<org.fauxgartic.grpc.Jogador, org.fauxgartic.grpc.EventoDeJogo> getReceberEventosMethod;
    if ((getReceberEventosMethod = FauxGarticServiceGrpc.getReceberEventosMethod) == null) {
      synchronized (FauxGarticServiceGrpc.class) {
        if ((getReceberEventosMethod = FauxGarticServiceGrpc.getReceberEventosMethod) == null) {
          FauxGarticServiceGrpc.getReceberEventosMethod = getReceberEventosMethod =
              io.grpc.MethodDescriptor.<org.fauxgartic.grpc.Jogador, org.fauxgartic.grpc.EventoDeJogo>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ReceberEventos"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.fauxgartic.grpc.Jogador.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.fauxgartic.grpc.EventoDeJogo.getDefaultInstance()))
              .setSchemaDescriptor(new FauxGarticServiceMethodDescriptorSupplier("ReceberEventos"))
              .build();
        }
      }
    }
    return getReceberEventosMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.fauxgartic.grpc.AcaoJogador,
      org.fauxgartic.grpc.Vazio> getEnviarAcaoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "EnviarAcao",
      requestType = org.fauxgartic.grpc.AcaoJogador.class,
      responseType = org.fauxgartic.grpc.Vazio.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.fauxgartic.grpc.AcaoJogador,
      org.fauxgartic.grpc.Vazio> getEnviarAcaoMethod() {
    io.grpc.MethodDescriptor<org.fauxgartic.grpc.AcaoJogador, org.fauxgartic.grpc.Vazio> getEnviarAcaoMethod;
    if ((getEnviarAcaoMethod = FauxGarticServiceGrpc.getEnviarAcaoMethod) == null) {
      synchronized (FauxGarticServiceGrpc.class) {
        if ((getEnviarAcaoMethod = FauxGarticServiceGrpc.getEnviarAcaoMethod) == null) {
          FauxGarticServiceGrpc.getEnviarAcaoMethod = getEnviarAcaoMethod =
              io.grpc.MethodDescriptor.<org.fauxgartic.grpc.AcaoJogador, org.fauxgartic.grpc.Vazio>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "EnviarAcao"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.fauxgartic.grpc.AcaoJogador.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.fauxgartic.grpc.Vazio.getDefaultInstance()))
              .setSchemaDescriptor(new FauxGarticServiceMethodDescriptorSupplier("EnviarAcao"))
              .build();
        }
      }
    }
    return getEnviarAcaoMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static FauxGarticServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FauxGarticServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FauxGarticServiceStub>() {
        @java.lang.Override
        public FauxGarticServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FauxGarticServiceStub(channel, callOptions);
        }
      };
    return FauxGarticServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static FauxGarticServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FauxGarticServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FauxGarticServiceBlockingStub>() {
        @java.lang.Override
        public FauxGarticServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FauxGarticServiceBlockingStub(channel, callOptions);
        }
      };
    return FauxGarticServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static FauxGarticServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FauxGarticServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FauxGarticServiceFutureStub>() {
        @java.lang.Override
        public FauxGarticServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FauxGarticServiceFutureStub(channel, callOptions);
        }
      };
    return FauxGarticServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * MUDANÇA: Nome do serviço atualizado para FauxGarticService
   * </pre>
   */
  public interface AsyncService {

    /**
     */
    default void entrarNoJogo(org.fauxgartic.grpc.Jogador request,
        io.grpc.stub.StreamObserver<org.fauxgartic.grpc.EstadoDoJogo> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getEntrarNoJogoMethod(), responseObserver);
    }

    /**
     */
    default void receberEventos(org.fauxgartic.grpc.Jogador request,
        io.grpc.stub.StreamObserver<org.fauxgartic.grpc.EventoDeJogo> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReceberEventosMethod(), responseObserver);
    }

    /**
     */
    default void enviarAcao(org.fauxgartic.grpc.AcaoJogador request,
        io.grpc.stub.StreamObserver<org.fauxgartic.grpc.Vazio> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getEnviarAcaoMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service FauxGarticService.
   * <pre>
   * MUDANÇA: Nome do serviço atualizado para FauxGarticService
   * </pre>
   */
  public static abstract class FauxGarticServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return FauxGarticServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service FauxGarticService.
   * <pre>
   * MUDANÇA: Nome do serviço atualizado para FauxGarticService
   * </pre>
   */
  public static final class FauxGarticServiceStub
      extends io.grpc.stub.AbstractAsyncStub<FauxGarticServiceStub> {
    private FauxGarticServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FauxGarticServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FauxGarticServiceStub(channel, callOptions);
    }

    /**
     */
    public void entrarNoJogo(org.fauxgartic.grpc.Jogador request,
        io.grpc.stub.StreamObserver<org.fauxgartic.grpc.EstadoDoJogo> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getEntrarNoJogoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void receberEventos(org.fauxgartic.grpc.Jogador request,
        io.grpc.stub.StreamObserver<org.fauxgartic.grpc.EventoDeJogo> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getReceberEventosMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void enviarAcao(org.fauxgartic.grpc.AcaoJogador request,
        io.grpc.stub.StreamObserver<org.fauxgartic.grpc.Vazio> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getEnviarAcaoMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service FauxGarticService.
   * <pre>
   * MUDANÇA: Nome do serviço atualizado para FauxGarticService
   * </pre>
   */
  public static final class FauxGarticServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<FauxGarticServiceBlockingStub> {
    private FauxGarticServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FauxGarticServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FauxGarticServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.fauxgartic.grpc.EstadoDoJogo entrarNoJogo(org.fauxgartic.grpc.Jogador request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getEntrarNoJogoMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<org.fauxgartic.grpc.EventoDeJogo> receberEventos(
        org.fauxgartic.grpc.Jogador request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getReceberEventosMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.fauxgartic.grpc.Vazio enviarAcao(org.fauxgartic.grpc.AcaoJogador request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getEnviarAcaoMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service FauxGarticService.
   * <pre>
   * MUDANÇA: Nome do serviço atualizado para FauxGarticService
   * </pre>
   */
  public static final class FauxGarticServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<FauxGarticServiceFutureStub> {
    private FauxGarticServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FauxGarticServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FauxGarticServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.fauxgartic.grpc.EstadoDoJogo> entrarNoJogo(
        org.fauxgartic.grpc.Jogador request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getEntrarNoJogoMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.fauxgartic.grpc.Vazio> enviarAcao(
        org.fauxgartic.grpc.AcaoJogador request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getEnviarAcaoMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ENTRAR_NO_JOGO = 0;
  private static final int METHODID_RECEBER_EVENTOS = 1;
  private static final int METHODID_ENVIAR_ACAO = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ENTRAR_NO_JOGO:
          serviceImpl.entrarNoJogo((org.fauxgartic.grpc.Jogador) request,
              (io.grpc.stub.StreamObserver<org.fauxgartic.grpc.EstadoDoJogo>) responseObserver);
          break;
        case METHODID_RECEBER_EVENTOS:
          serviceImpl.receberEventos((org.fauxgartic.grpc.Jogador) request,
              (io.grpc.stub.StreamObserver<org.fauxgartic.grpc.EventoDeJogo>) responseObserver);
          break;
        case METHODID_ENVIAR_ACAO:
          serviceImpl.enviarAcao((org.fauxgartic.grpc.AcaoJogador) request,
              (io.grpc.stub.StreamObserver<org.fauxgartic.grpc.Vazio>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getEntrarNoJogoMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              org.fauxgartic.grpc.Jogador,
              org.fauxgartic.grpc.EstadoDoJogo>(
                service, METHODID_ENTRAR_NO_JOGO)))
        .addMethod(
          getReceberEventosMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              org.fauxgartic.grpc.Jogador,
              org.fauxgartic.grpc.EventoDeJogo>(
                service, METHODID_RECEBER_EVENTOS)))
        .addMethod(
          getEnviarAcaoMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              org.fauxgartic.grpc.AcaoJogador,
              org.fauxgartic.grpc.Vazio>(
                service, METHODID_ENVIAR_ACAO)))
        .build();
  }

  private static abstract class FauxGarticServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    FauxGarticServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.fauxgartic.grpc.Fauxgartic.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("FauxGarticService");
    }
  }

  private static final class FauxGarticServiceFileDescriptorSupplier
      extends FauxGarticServiceBaseDescriptorSupplier {
    FauxGarticServiceFileDescriptorSupplier() {}
  }

  private static final class FauxGarticServiceMethodDescriptorSupplier
      extends FauxGarticServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    FauxGarticServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (FauxGarticServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new FauxGarticServiceFileDescriptorSupplier())
              .addMethod(getEntrarNoJogoMethod())
              .addMethod(getReceberEventosMethod())
              .addMethod(getEnviarAcaoMethod())
              .build();
        }
      }
    }
    return result;
  }
}
