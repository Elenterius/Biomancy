package com.github.elenterius.biomancy.client.renderer.tileentity;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.block.FleshChestBlock;
import com.github.elenterius.biomancy.init.ModBlocks;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FleshChestTileEntityRenderer<T extends TileEntity & IChestLid> extends TileEntityRenderer<T> {

	public static final ResourceLocation FLESH_CHEST_TEXTURE = BiomancyMod.createRL("entity/flesh_chest");
	public static final RenderMaterial FLESH_CHEST_MATERIAL = new RenderMaterial(Atlases.CHEST_ATLAS, FLESH_CHEST_TEXTURE);

	private final ModelRenderer chestLid;
	private final ModelRenderer chestBottom;
	private final ModelRenderer chestLatch;

	public FleshChestTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		chestBottom = new ModelRenderer(64, 64, 0, 19);
		chestBottom.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
		chestLid = new ModelRenderer(64, 64, 0, 0);
		chestLid.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
		chestLid.rotationPointY = 9.0F;
		chestLid.rotationPointZ = 1.0F;
		chestLatch = new ModelRenderer(64, 64, 0, 0);
		chestLatch.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
		chestLatch.rotationPointY = 8.0F;
	}

	@Override
	public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		World world = tileEntityIn.getWorld();
		BlockState state = world != null ? tileEntityIn.getBlockState() : ModBlocks.FLESHBORN_CHEST.get().getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
		Block block = state.getBlock();
		if (block instanceof FleshChestBlock) {
			matrixStackIn.push();

			float angle = state.get(FleshChestBlock.FACING).getHorizontalAngle();
			matrixStackIn.translate(0.5d, 0.5d, 0.5d);
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-angle));
			matrixStackIn.translate(-0.5d, -0.5d, -0.5d);

			float lidAngle = 1f - (float) Math.pow(1f - tileEntityIn.getLidAngle(partialTicks), 3);
			chestLid.rotateAngleX = -(lidAngle * ((float) Math.PI / 2f));
			chestLatch.rotateAngleX = chestLid.rotateAngleX;

			IVertexBuilder ivertexbuilder = FLESH_CHEST_MATERIAL.getBuffer(bufferIn, RenderType::getEntityCutout);
			chestLid.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
			chestLatch.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
			chestBottom.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);

			matrixStackIn.pop();
		}
	}
}
