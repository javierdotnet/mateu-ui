package io.mateu.ui.teavm.plugin;

import org.teavm.vm.spi.TeaVMHost;
import org.teavm.vm.spi.TeaVMPlugin;

/**
 * Created by miguel on 15/8/17.
 */
public class MateuPlugin implements TeaVMPlugin {

    public void install(TeaVMHost host) {
        host.add(new MateuTransformer());
    }
}