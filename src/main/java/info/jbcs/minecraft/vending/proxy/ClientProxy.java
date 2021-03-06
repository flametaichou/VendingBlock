package info.jbcs.minecraft.vending.proxy;

import info.jbcs.minecraft.vending.Vending;
import info.jbcs.minecraft.vending.block.BlockVendingMachine;
import info.jbcs.minecraft.vending.block.BlockVendingStorageAttachment;
import info.jbcs.minecraft.vending.block.EnumSupports;
import info.jbcs.minecraft.vending.gui.hud.HintHUD;
import info.jbcs.minecraft.vending.init.VendingBlocks;
import info.jbcs.minecraft.vending.init.VendingItems;
import info.jbcs.minecraft.vending.renderer.TileEntityVendingMachineRenderer;
import info.jbcs.minecraft.vending.renderer.TileEntityVendingStorageAttachmentRenderer;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingMachine;
import info.jbcs.minecraft.vending.tileentity.TileEntityVendingStorageAttachment;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {
    private Minecraft mc;

    @Override
    public void registerEventHandlers() {
        MinecraftForge.EVENT_BUS.register(new HintHUD(Minecraft.getMinecraft()));
    }

    @Override
    public void registerRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVendingMachine.class, new TileEntityVendingMachineRenderer());
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        renderItem.getItemModelMesher().register(VendingItems.ITEM_WRENCH, 0, new ModelResourceLocation(Vending.MOD_ID + ":" + "vendingMachineWrench", "inventory"));
        for (int i = 0; i < EnumSupports.length; i++) {
            registerVendingMachineRenderer(renderItem, VendingBlocks.BLOCK_VENDING_MACHINE, i);
            registerVendingMachineRenderer(renderItem, VendingBlocks.BLOCK_VENDING_MACHINE_ADVANCED, i);
            registerVendingMachineRenderer(renderItem, VendingBlocks.BLOCK_VENDING_MACHINE_MULTIPLE, i);
        }
        registerTESRRender(VendingBlocks.BLOCK_VENDING_STORAGE_ATTACHMENT, new TileEntityVendingStorageAttachmentRenderer(),
                TileEntityVendingStorageAttachment.class);
    }

    private void registerVendingMachineRenderer(RenderItem renderItem, BlockVendingMachine block, int i) {
        renderItem.getItemModelMesher().register(Item.getItemFromBlock(block), i,
                new ModelResourceLocation(Vending.MOD_ID + ":" + block.getName(),
                        "support=" + EnumSupports.byMetadata(i).getUnlocalizedName()));
    }

    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return (ctx.side.isClient() ? mc.player : super.getPlayerEntity(ctx));
    }

    @Override
    public IThreadListener getThreadFromContext(MessageContext ctx) {
        return (ctx.side.isClient() ? mc : super.getThreadFromContext(ctx));
    }

    private void registerTESRRender(Block block, TileEntitySpecialRenderer renderer, Class<? extends TileEntity> te) {
        ClientRegistry.bindTileEntitySpecialRenderer(te, renderer);
        Item item = Item.getItemFromBlock(block);
        ForgeHooksClient.registerTESRItemStack(item, 0, te);
        ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockVendingStorageAttachment.FACING).build());
    }
}
