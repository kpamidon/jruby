package org.jruby.javasupport;

import org.jruby.RubyClass;
import org.jruby.RubyProc;
import org.jruby.anno.JRubyMethod;
import org.jruby.anno.JRubyModule;
import org.jruby.runtime.Block;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.Visibility;
import org.jruby.runtime.builtin.IRubyObject;

@JRubyModule(name = "JavaUtilities")
public class JavaUtilities {
    @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
    public static IRubyObject set_java_object(IRubyObject recv, IRubyObject self, IRubyObject java_object) {
        self.dataWrapStruct(java_object);
        return java_object;
    }

    @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
    public static IRubyObject get_interface_module(IRubyObject recv, IRubyObject arg0) {
        return Java.get_interface_module(recv.getRuntime(), arg0);
    }

    @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
    public static IRubyObject get_package_module(IRubyObject recv, IRubyObject arg0) {
        return Java.get_package_module(recv, arg0);
    }

    @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
    public static IRubyObject get_package_module_dot_format(IRubyObject recv, IRubyObject arg0) {
        return Java.get_package_module_dot_format(recv, arg0);
    }

    @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
    public static IRubyObject get_proxy_class(IRubyObject recv, IRubyObject arg0) {
        return Java.get_proxy_class(recv, arg0);
    }

    @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
    public static IRubyObject create_proxy_class(IRubyObject recv, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
        return Java.create_proxy_class(recv, arg0, arg1, arg2);
    }

    @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
    public static IRubyObject get_java_class(IRubyObject recv, IRubyObject arg0) {
        return Java.get_java_class(recv, arg0);
    }

    @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
    public static IRubyObject get_top_level_proxy_or_package(ThreadContext context, IRubyObject recv, IRubyObject arg0) {
        return Java.get_top_level_proxy_or_package(context, recv, arg0);
    }

    @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
    public static IRubyObject get_proxy_or_package_under_package(ThreadContext context, IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
        return Java.get_proxy_or_package_under_package(context, recv, arg0, arg1);
    }

    @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
    public static IRubyObject register_converter(ThreadContext context, IRubyObject recv, IRubyObject source, IRubyObject target, IRubyObject converter, Block block) {
        if (block.isGiven()) {
            context.runtime.getWarnings().warn("block passed to `register_converter' ignored");
        }

        if (!(source instanceof RubyClass)) {
            throw context.runtime.newTypeError(source, context.runtime.getClassClass());
        }

        Object maybeTarget = target.dataGetStruct();
        if (!(maybeTarget instanceof Class)) context.runtime.newTypeError(target, Java.getProxyClass(context.runtime, Class.class));

        context.runtime.getJavaSupport().registerConverter((RubyClass)source, (Class)maybeTarget, converter);

        return context.nil;
    }

    @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
    public static IRubyObject register_converter(ThreadContext context, IRubyObject recv, IRubyObject source, IRubyObject target, Block block) {
        return register_converter(context, recv, source, target, RubyProc.newProc(context.runtime, block, Block.Type.LAMBDA), Block.NULL_BLOCK);
    }
}
