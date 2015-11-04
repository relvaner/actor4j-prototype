package actor4j.core.features;

import java.util.UUID;

import actor4j.core.ActorSystem;
import actor4j.core.actors.Actor;
import actor4j.core.messages.ActorMessage;
import actor4j.core.utils.ActorFactory;

public class UnhandledFeature {
	protected ActorSystem system;

	public void before() {
		system = new ActorSystem();
		system.setParallelismMin(1);
		system.setDebugUnhandled(true);
	}
	
	public void test() {
		UUID dest = system.addActor(new ActorFactory() { 
			@Override
			public Actor create() {
				return new Actor("UnhandledFeatureActor") {
					@Override
					public void receive(ActorMessage<?> message) {
						unhandled(message);
					}
				};
			}
		});
		
		system.send(new ActorMessage<Object>(null, 0, system.SYSTEM_ID, dest));
		system.start();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		system.shutdown(true);
	}
	
	public static void main(String[] args) {
		UnhandledFeature unhandledFeature = new UnhandledFeature();
		unhandledFeature.before();
		unhandledFeature.test();
	}
}