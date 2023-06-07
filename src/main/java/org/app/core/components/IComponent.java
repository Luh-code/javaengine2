package org.app.core.components;

import org.app.ecs.ECS;

import static org.app.utils.Logger.logDebug;

public interface IComponent {
    static void registerComponent(ECS ecs) {
        Object myself = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(s -> s.map(StackWalker.StackFrame::getDeclaringClass).findFirst());
        logDebug("Registering '" + myself.getClass().getSimpleName() + "' as component in ECS '" + ecs + "'");
        ecs.registerComponent_s(myself.getClass());
    }
}
