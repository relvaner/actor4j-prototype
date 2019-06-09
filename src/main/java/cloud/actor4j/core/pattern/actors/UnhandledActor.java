/*
 * Copyright (c) 2015-2017, David A. Bauer. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cloud.actor4j.core.pattern.actors;

import java.util.function.Consumer;

import cloud.actor4j.core.actors.Actor;
import cloud.actor4j.core.messages.ActorMessage;

public class UnhandledActor extends Actor {
	protected Consumer<ActorMessage<?>> handler;
	
	public UnhandledActor(Consumer<ActorMessage<?>> handler) {
		this(null, handler);
	}
	
	public UnhandledActor(String name, Consumer<ActorMessage<?>> handler) {
		super(name);
		
		this.handler = handler;
	}
	
	@Override
	public void receive(ActorMessage<?> message) {
		handler.accept(message);
	}
}