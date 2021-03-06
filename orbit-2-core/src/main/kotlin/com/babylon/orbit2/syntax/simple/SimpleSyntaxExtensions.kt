/*
 * Copyright 2020 Babylon Partners Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.babylon.orbit2.syntax.simple

import com.babylon.orbit2.Container
import com.babylon.orbit2.ContainerHost
import com.babylon.orbit2.idling.withIdling
import com.babylon.orbit2.syntax.Orbit2Dsl

/**
 * Reducers reduce the current state and incoming events to produce a new state.
 *
 * @param reducer the lambda reducing the current state and incoming event to produce a new state
 */
@Orbit2Dsl
public suspend fun <S : Any, SE : Any> SimpleSyntax<S, SE>.reduce(reducer: SimpleContext<S>.() -> S) {
    containerContext.apply {
        reduce { reducerState ->
            object : SimpleContext<S> {
                override val state: S = reducerState
            }.reducer()
        }
    }
}

/**
 * Side effects allow you to deal with things like tracking, navigation etc.
 *
 * These are delivered through [Container.sideEffectFlow] by calling [SimpleSyntax.postSideEffect].
 *
 * @param sideEffect the side effect to post through the side effect flow
 */
@Orbit2Dsl
public suspend fun <S : Any, SE : Any> SimpleSyntax<S, SE>.postSideEffect(sideEffect: SE) {
    containerContext.postSideEffect(sideEffect)
}

/**
 * Build and execute an intent on [Container].
 *
 * @param registerIdling whether to register an idling resource when executing this intent. Defaults to true.
 * @param transformer lambda representing the transformer
 */
@Orbit2Dsl
public fun <STATE : Any, SIDE_EFFECT : Any> ContainerHost<STATE, SIDE_EFFECT>.intent(
    registerIdling: Boolean = true,
    transformer: suspend SimpleSyntax<STATE, SIDE_EFFECT>.() -> Unit
): Unit =
    container.orbit {
        withIdling(registerIdling) {
            SimpleSyntax(this).transformer()
        }
    }
