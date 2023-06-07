package org.app.core.data;

import org.app.ecs.ECS;

import static org.app.utils.Logger.logDebug;

public interface IResource {
    static void registerResource(ECS ecs) {
        Object myself = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(s -> s.map(StackWalker.StackFrame::getDeclaringClass).findFirst());
        logDebug("Registering '" + myself.getClass().getSimpleName() + "' as resource in ECS '" + ecs + "'");
        ecs.registerResourceType_s(myself.getClass());
    }
}
