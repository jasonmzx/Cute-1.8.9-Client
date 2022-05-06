package cute.modules.test;

import cute.eventapi.EventTarget;
import cute.events.ClientTickEvent;

import cute.modules.Module;
import cute.modules.enums.Category;

public class TestModule extends Module 
{
	public TestModule()
	{
		super("Test 1", Category.BOT, "this is for testing events");
	}
	
	
	@EventTarget
	public void onTick(ClientTickEvent event)
	{
		System.out.println("client tick event");
	}
}