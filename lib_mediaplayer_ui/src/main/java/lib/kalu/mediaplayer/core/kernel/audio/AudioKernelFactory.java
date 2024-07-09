package lib.kalu.mediaplayer.core.kernel.audio;


public interface AudioKernelFactory<T extends AudioKernelApi> {
    T createKernel();
}