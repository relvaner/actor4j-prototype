/*
 * Copyright (c) 2015-2018, David A. Bauer. All rights reserved.
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
package cloud.actor4j.core.features;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;

import cloud.actor4j.core.ActorSystem;
import cloud.actor4j.core.actors.Actor;
import cloud.actor4j.core.actors.ResourceActor;
import cloud.actor4j.core.annotations.Stateless;
import cloud.actor4j.core.messages.ActorMessage;

public class ResourceActorFeature {
	@Stateless
	protected static class StatelessResourceActor extends ResourceActor {
		public StatelessResourceActor() {
			super();
		}

		public StatelessResourceActor(String name) {
			super(name);
		}

		@Override
		public void receive(ActorMessage<?> message) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			tell(null, 1, message.source);
		}
	}
	
	protected ActorSystem system;
	
	@Before
	public void before() {
		system = new ActorSystem();
	}
	
	@Test(timeout=5000)
	public void test_stateless() {
		CountDownLatch testDone = new CountDownLatch(5);
		
		UUID resource = system.addActor(() -> new StatelessResourceActor("resource"));
		UUID parent = system.addActor(() -> new Actor("parent") {
			protected UUID child;
			
			@Override
			public void preStart() {	
				child = addChild(() -> new Actor("child") {
					@Override
					public void receive(ActorMessage<?> message) {
						if (message.tag==1 && message.source.equals(resource))
							testDone.countDown();
					}
				});
			}
			@Override
			public void receive(ActorMessage<?> message) {
				send(new ActorMessage<>(null, 0, child, resource));
			}
		});
		
		system.start();
		
		system.send(new ActorMessage<>(null, 0, system.SYSTEM_ID, parent));
		system.send(new ActorMessage<>(null, 0, system.SYSTEM_ID, parent));
		system.send(new ActorMessage<>(null, 0, system.SYSTEM_ID, parent));
		system.send(new ActorMessage<>(null, 0, system.SYSTEM_ID, parent));
		system.send(new ActorMessage<>(null, 0, system.SYSTEM_ID, parent));
		try {
			testDone.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		system.shutdownWithActors(true);
	}
	
	@Test(timeout=5000)
	public void test_statefull() {
		CountDownLatch testDone = new CountDownLatch(5);
		
		UUID resource = system.addActor(() -> new ResourceActor("resource") {
			@Override
			public void receive(ActorMessage<?> message) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				tell(null, 1, message.source);
			}
		});
		
		UUID parent = system.addActor(() -> new Actor("parent") {
			protected UUID child;
			
			@Override
			public void preStart() {	
				child = addChild(() -> new Actor("child") {
					@Override
					public void receive(ActorMessage<?> message) {
						if (message.tag==1 && message.source.equals(resource))
							testDone.countDown();
					}
				});
			}
			@Override
			public void receive(ActorMessage<?> message) {
				send(new ActorMessage<>(null, 0, child, resource));
			}
		});
		
		system.start();
		
		system.send(new ActorMessage<>(null, 0, system.SYSTEM_ID, parent));
		system.send(new ActorMessage<>(null, 0, system.SYSTEM_ID, parent));
		system.send(new ActorMessage<>(null, 0, system.SYSTEM_ID, parent));
		system.send(new ActorMessage<>(null, 0, system.SYSTEM_ID, parent));
		system.send(new ActorMessage<>(null, 0, system.SYSTEM_ID, parent));
		try {
			testDone.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		system.shutdownWithActors(true);
	}
}