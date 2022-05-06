package cute.util.types;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class BlockInfo 
{
	// the creative menu block name
	public final String displayName;
	
	// the registry id / name (minecraft:block_name) 
	public final String registryName;
	
	public final Block block;
	
	public final ResourceLocation location;
	
	public BlockInfo(ResourceLocation loc)
	{
		this.location = loc;
		
		this.registryName = loc.toString();
		
		this.block = Block.blockRegistry.getObject(loc);
		
		if(this.registryName.compareTo("minecraft:end_portal_frame") == 0)
		{
			this.displayName = "End Portal Frame";
			return;
		}
		
		if(this.registryName.compareTo("minecraft:end_portal") == 0)
		{
			this.displayName = "End Portal (actual?)";
			return;
		}
		
		if(this.registryName.compareTo("minecraft:piston_extension") == 0)
		{
			this.displayName = "Piston Extension";
			return;
		}
		
		this.displayName = this.block.getLocalizedName();
	}
}