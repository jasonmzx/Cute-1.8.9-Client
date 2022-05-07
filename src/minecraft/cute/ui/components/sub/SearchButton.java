package cute.ui.components.sub;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cute.settings.ListSelection;
import cute.settings.enums.ListType;
import cute.ui.ClickUI;
import cute.ui.components.Button;
import cute.ui.components.Component;
import cute.util.Cache;
import cute.util.FontUtil;
import cute.util.RenderUtil;
import cute.util.types.BlockInfo;
import cute.util.types.VirtualBlock;
import net.minecraft.block.Block;
import net.minecraft.potion.Potion;

public class SearchButton extends Component  
{
	private final Button parent;
	
	private boolean hovered;
	private boolean binding;
	
	private int offset;
	private int x;
	private int y;
	
	private int scrollButtonSize = this.height - 3;
	private int scrollIndex = 0;
	
	private Object[] foundSearchTerms = new Object[0];
	
	private ListType type;
	
	private String searchTerm = "";
	
	ListSelection setting;
	
	private int listCap = 10;
	
	public SearchButton(Button button, int offset, ListSelection setting)
	{
		this.parent = button;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
		this.type = setting.getListType();
		this.setting = setting;
	}
	
	@Override
	public int getHeight() 
	{
		return this.height;
	}

	
	public int getListHeight()
	{
		return this.height * Math.min(this.listCap, this.foundSearchTerms.length) + this.scrollButtonSize * 2;
	}
	
	public int getListY()
	{
		return this.y - this.scrollButtonSize;
	}
	
	public int getListX()
	{
		return this.x + this.width;
	}
	
	@Override
	public void setOff(int newOff) 
	{
		this.offset = newOff;
	}
	
	@Override
	public void renderComponent() 
	{
		RenderUtil.beginRenderRect();
		
//		background 
		RenderUtil.setColor(this.backColor);
		RenderUtil.renderRect(x + 2, y, x + width, y + this.getHeight());
		RenderUtil.renderRect(x    , y, x + 2    , y + this.getHeight());
		RenderUtil.endRenderRect();
		
		// draw the text for the setting 
		GL11.glPushMatrix();
		GL11.glScalef(0.75f,0.75f, 0.75f);
		
		String text = this.binding ? this.searchTerm : "Search";
		
		FontUtil.drawStringWithShadow(
				text, 
				(this.x + 3) * this.tScale + 4, 
				(this.y + 2) * this.tScale + 2,
				this.textColorInt);
		
		GL11.glPopMatrix();
		
		
		// if the block list isn't open we're done here
		if(!this.binding)
			return;
		
		int lx = this.getListX();
		int ly = this.getListY();
		int range = Math.min(this.foundSearchTerms.length, scrollIndex + this.listCap);
		
		// render background for the list of blocks 
		RenderUtil.setColor(this.backColor);
		RenderUtil.renderRectSingle(lx + 2, ly, lx + width, ly + this.getListHeight());
		
		GL11.glPushMatrix();
		GL11.glScalef(0.75f,0.75f, 0.75f);
		
		// render up and down arrows for the scroll buttons 
		FontUtil.drawStringWithShadow(
				"/\\     " + String.valueOf(scrollIndex) + "-" + String.valueOf(range) + "/" + String.valueOf(this.foundSearchTerms.length), 
				lx * this.tScale + 4, 
				ly * this.tScale + 4, 
				this.textColorInt);
		
		FontUtil.drawStringWithShadow(
				"\\/", 
				lx * this.tScale + 4, 
				(ly + this.getListHeight() - this.scrollButtonSize) * this.tScale + 2, 
				this.textColorInt);
		
		
		// render all the blocks 
		ly += this.scrollButtonSize;
		
		for(int i = scrollIndex; i < Math.min(this.foundSearchTerms.length, scrollIndex + this.listCap); i++)
		{
			String display;
			
			display = ((BlockInfo)this.foundSearchTerms[i]).displayName;
			
			FontUtil.drawStringWithShadow(
					display + " ", 
					lx * this.tScale + 4, 
					ly * this.tScale + 4, 
					this.textColorInt);

			ly += this.height;
		}
		
		GL11.glPopMatrix();
	}
	
	@Override
	public void updateComponent(int mouseX, int mouseY) 
	{
		this.y = parent.parent.getY() + offset;
		this.x = parent.parent.getX();
	}
	
	public int getListHoverIndex(int x, int y)
	{
		return (int)((y - this.y) / this.height);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) 
	{
		if(!this.parent.isOpen())
			return;
		
		if(button == 1)
		{
			if(!isMouseOnButton(mouseX, mouseY))
				return;
			
			this.setBinding(false);
			return;
		}
		
		if(button == 0)
		{
			if(this.isMouseOnButton(mouseX, mouseY))
			{
				this.setBinding(true);
				return;
			}
			
			if(this.isMouseOnList(mouseX, mouseY))
			{
				int index = this.getListHoverIndex(mouseX, mouseY);
				
				if(index >= 0 && index < this.foundSearchTerms.length)
				{
					this.setBinding(false);
					VirtualBlock vb = new VirtualBlock(((BlockInfo)this.foundSearchTerms[index]).location);
					vb.enabled = true;
					this.setting.enableItem(vb);
				}
				return;
			}
			
			if(this.isMouseOnScrollUp(mouseX, mouseY))
			{
				this.scrollIndex = Math.max(0, this.scrollIndex - 1);
				return;
			}
			
			if(this.isMouseOnScrollDown(mouseX, mouseY))
			{	
				if(this.foundSearchTerms.length <= this.listCap)
					return;
				
				this.scrollIndex = Math.min(this.foundSearchTerms.length - this.listCap, this.scrollIndex + 1);
				return;
			}
			
		}		
	}	
	
	public void setBinding(boolean state)
	{
		this.binding = state;
		ClickUI.keyboardInUses = state;
	}
	
	@Override
	public boolean isOpen()
	{
		return this.foundSearchTerms.length != 0;
	}
	
	@Override
	public void keyTyped(char typedChar, int key) 
	{
		if(!this.binding) 
			return;
		
		
		switch(key)
		{
			case Keyboard.KEY_ESCAPE:
				this.setBinding(false);
				return;
				
			case Keyboard.KEY_BACK:
				if(this.searchTerm.length() == 0)
					return;
				this.searchTerm = this.searchTerm.substring(0, this.searchTerm.length() - 1);
				return;
				
			case Keyboard.KEY_RETURN:
				this.scrollIndex = 0;
				this.foundSearchTerms = Cache.searchForBlock(this.searchTerm.toLowerCase());
				return;
				
			default:
				if(typedChar >= 'a' && typedChar <= 'z' ||
				   typedChar >= 'A' && typedChar <= 'Z' || 
				   typedChar == ' ')
				{
					this.searchTerm += typedChar;
				}
				return;
		}
	}
	

	public boolean isMouseOnScrollUp(int x, int y)
	{
		if(!this.isOpen())
			return false;
		
		int lx = this.getListX();
		int ly = this.getListY();
		
		return x > lx && 
			   x < lx + this.width && 
			   y > ly &&
			   y < ly + this.scrollButtonSize;
	}
	
	public boolean isMouseOnScrollDown(int x, int y)
	{
		if(!this.isOpen())
			return false;
		
		int lx = this.getListX();
		int ly = this.getListY() + this.getListHeight() - this.scrollButtonSize;
		
		return x > lx && 
			   x < lx + this.width && 
			   y > ly &&
			   y < ly + this.scrollButtonSize;
	}
	
	
	public boolean isMouseOnList(int x, int y)
	{
		if(!this.isOpen())
			return false;
		
		int lx = this.getListX();
		int ly = this.y;
		int height = this.getListHeight() - this.scrollButtonSize*2;
		
		return x > lx && 
			   x < lx + this.width && 
			   y > ly &&
			   y < ly + height;
	}
	
	public boolean isMouseOnButton(int x, int y) 
	{
		return x > this.x && 
			   x < this.x + this.width && 
			   y > this.y &&
			   y < this.y + this.height;
	}
}




