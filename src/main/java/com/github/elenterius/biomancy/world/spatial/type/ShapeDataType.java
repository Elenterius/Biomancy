package com.github.elenterius.biomancy.world.spatial.type;

import com.github.elenterius.biomancy.util.serialization.NBTSerializer;
import com.github.elenterius.biomancy.world.spatial.geometry.Shape;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.type.BasicDataType;
import org.h2.util.Utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ShapeDataType extends BasicDataType<Shape> {

	private final Object2IntOpenHashMap<Class<?>> averageSizes = new Object2IntOpenHashMap<>();

	public static byte[] writeCompressed(CompoundTag compoundTag) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try (DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(byteArrayOutputStream))) {
			NbtIo.write(compoundTag, dataoutputstream);
		}
		catch (IOException e) {
			return Utils.EMPTY_BYTES;
		}

		return byteArrayOutputStream.toByteArray();
	}

	public static CompoundTag readCompressed(byte[] bytes) {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

		try (DataInputStream inputStream = new DataInputStream(new GZIPInputStream(byteArrayInputStream))) {
			return NbtIo.read(inputStream, NbtAccounter.UNLIMITED);
		}
		catch (IOException e) {
			return new CompoundTag();
		}
	}

	@Override
	public int getMemory(Shape shape) {
		return averageSizes.getOrDefault(shape.getClass(), 1000);
	}

	private void computeAverageSize(Shape shape, byte[] data) {
		int averageSize = averageSizes.getOrDefault(shape.getClass(), 1000);
		int size = data.length * 2;
		long n = 15L;
		averageSize = (int) ((size + n * averageSize) / (n + 1)); //exponential moving average
		averageSizes.put(shape.getClass(), averageSize);
	}

	@Override
	public void write(WriteBuffer buffer, Shape shape) {
		if (shape == Shape.EMPTY) {
			buffer.putVarInt(0);
			return;
		}

		NBTSerializer<Shape> serializer = shape.getNBTSerializer();
		CompoundTag nbt = serializer.write(shape);
		nbt.putString("Serializer", serializer.id());

		byte[] data = writeCompressed(nbt);
		buffer.putVarInt(data.length);
		if (data.length > 0) {
			buffer.put(data);
			computeAverageSize(shape, data);
		}
	}

	@Override
	public Shape read(ByteBuffer buffer) {
		int length = DataUtils.readVarInt(buffer);
		if (length > 0) {
			byte[] data = new byte[length];
			buffer.get(data);
			CompoundTag nbt = readCompressed(data);

			String serializerId = nbt.getString("Serializer");
			NBTSerializer<Shape> serializer = ShapeSerializers.get(serializerId);
			if (serializer != null) {
				Shape shape = serializer.read(nbt);
				computeAverageSize(shape, data);
				return shape;
			}
		}

		return Shape.EMPTY;
	}

	@Override
	public Shape[] createStorage(int size) {
		return new Shape[size];
	}

}
