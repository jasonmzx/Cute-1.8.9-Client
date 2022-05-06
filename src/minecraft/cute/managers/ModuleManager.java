package cute.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import cute.modules.enums.Category;
import cute.modules.gui.ClickGUI;
import cute.modules.render.ESPBlocks;
import cute.modules.render.ESPEntity;
import cute.modules.render.Fullbright;
import cute.modules.render.NoRender;
import cute.modules.render.ProjectileTracer;
import cute.modules.render.Tracers;
import cute.modules.test.TestModule;
import net.minecraft.client.gui.GuiScreen;
import cute.eventapi.EventManager;
import cute.eventapi.EventTarget;
import cute.events.ClientTickEvent;
import cute.events.KeyDownEvent;
import cute.events.KeyUpEvent;
import cute.events.KeyboardEvent;
import cute.modules.Module;

public class ModuleManager extends BaseManager
{
	public static final ModuleManager INSTANCE = new ModuleManager();
	
	private ModuleManager() 
	{
	}
	
	public static List<Module> modules = Arrays.asList
				(
//					new TestModule(),
					new ClickGUI(),
//					
					new ESPBlocks(),
					new ESPEntity(),
					new Fullbright(),
					new Tracers(),
					new ProjectileTracer(),
					new NoRender()
//					
//					new AntiPotion()
				);
	
	
	
	public static List<Module> getModules()
	{
		return new ArrayList(modules);
	}
	
	public static List<Module> getModulesInCategory(Category cat)
	{
		List<Module> module = new ArrayList();
		
		for (Module m : modules) 
		{
			if (m.getCategory().equals(cat))
				module.add(m);
		}
		
		return module;
	}
	
	public static Module getModuleByName(String name) 
	{
		return modules.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public static Module getModuleByClass(Class<?> clazz) 
	{
		return modules.stream().filter(module -> module.getClass().equals(clazz)).findFirst().orElse(null);
	}
	
	
	@EventTarget
	public void onTick(ClientTickEvent event) 
	{
		ModuleManager.onUpdate();
	}
	
//	@SideOnly(Side.CLIENT)
//	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
//	public void onFastTick(TickEvent event) 
//	{
//		ModuleManager.onFastUpdate();
//	}

	
	public static void onUpdate() 
	{
		for (Module m : modules) 
		{
			if (m.isEnabled())			
				m.onUpdate();
		}
	}
	
	public static void onFastUpdate() 
	{
		for (Module m : modules) 
		{
			if (m.isEnabled())
				m.onFastUpdate();
		}
	}
	
	public static void onServerUpdate() 
	{
		for (Module m : modules) 
		{
			if (m.isEnabled())
				m.onServerUpdate();
		}
	}
	

	@EventTarget
	public void keyDownEvent(KeyDownEvent event)
	{
		if(this.mc.currentScreen instanceof GuiScreen)
			return;
		
		int key = event.getKeyCode();
		
		for (Module m : modules) 
		{
			if(key != m.getKeyCode())
				continue;
			
			m.setKeyDown(true);
			m.toggle();
		}
	}

	@EventTarget
	public void keyUpEvent(KeyUpEvent event)
	{
		if(this.mc.currentScreen instanceof GuiScreen)
			return;
		
		int key = event.getKeyCode();
		
		for (Module m : modules) 
		{
			if(key != m.getKeyCode())
				continue;
			
			m.setKeyDown(false);
		}
	}
}





