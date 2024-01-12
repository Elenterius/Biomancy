package com.github.elenterius.biomancy.crafting.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

import java.util.Random;
import java.util.function.Function;

public interface ItemCountRange {

	enum RangeSerializerType {

		CONSTANT(0, "constant", ConstantValue.SERIALIZER),
		UNIFORM(1, "uniform", UniformRange.SERIALIZER),
		BINOMIAL(2, "binomial", BinomialRange.SERIALIZER);

		final byte id;
		final String type;
		final RangeSerializer<? extends ItemCountRange> serializer;

		RangeSerializerType(int id, String type, RangeSerializer<? extends ItemCountRange> serializer) {
			this.id = (byte) id;
			this.type = type;
			this.serializer = serializer;
		}

		public byte getId() {
			return id;
		}

		public String getType() {
			return type;
		}

		public static RangeSerializer<? extends ItemCountRange> getSerializer(String type) {
			for (RangeSerializerType available : values()) {
				if (type.equals(available.type)) {
					return available.serializer;
				}
			}
			throw new IllegalArgumentException("Invalid Type: " + type);
		}

		public static RangeSerializer<? extends ItemCountRange> getSerializer(byte id) {
			for (RangeSerializerType available : values()) {
				if (id == available.id) {
					return available.serializer;
				}
			}
			throw new IllegalArgumentException("Invalid Id: " + id);
		}

		public static String getType(RangeSerializer<? extends ItemCountRange> serializer) {
			return get(serializer, RangeSerializerType::getType);
		}

		public static byte getId(RangeSerializer<? extends ItemCountRange> serializer) {
			return get(serializer, RangeSerializerType::getId);
		}

		public static <T> T get(RangeSerializer<? extends ItemCountRange> serializer, Function<RangeSerializerType, T> func) {
			for (RangeSerializerType available : values()) {
				if (serializer == available.serializer) {
					return func.apply(available);
				}
			}
			throw new IllegalArgumentException("Invalid serializer: " + serializer);
		}

	}

	static <T extends ItemCountRange> void toJson(JsonObject jsonObject, T range) {
		//noinspection unchecked
		RangeSerializer<T> serializer = (RangeSerializer<T>) range.getSerializer();
		jsonObject.addProperty("type", RangeSerializerType.getType(serializer));
		serializer.toJson(jsonObject, range);
	}

	static ItemCountRange fromJson(JsonObject jsonObject) {
		String type = GsonHelper.getAsString(jsonObject, "type");
		return RangeSerializerType.getSerializer(type).fromJson(jsonObject);
	}

	static <T extends ItemCountRange> void toNetwork(FriendlyByteBuf buffer, T range) {
		//noinspection unchecked
		RangeSerializer<T> serializer = (RangeSerializer<T>) range.getSerializer();
		buffer.writeByte(RangeSerializerType.getId(serializer));
		serializer.toNetwork(buffer, range);
	}

	static ItemCountRange fromNetwork(FriendlyByteBuf buffer) {
		RangeSerializer<? extends ItemCountRange> serializer = RangeSerializerType.getSerializer(buffer.readByte());
		return serializer.fromNetwork(buffer);
	}

	int getCount(Random rng);

	RangeSerializer<?> getSerializer();

	interface RangeSerializer<T extends ItemCountRange> {
		void toJson(JsonObject jsonObject, T range);

		T fromJson(JsonObject jsonObject);

		void toNetwork(FriendlyByteBuf buffer, T range);

		T fromNetwork(FriendlyByteBuf buffer);
	}

	record UniformRange(int min, int max) implements ItemCountRange {

		public static final Serializer SERIALIZER = new Serializer();

		@Override
		public int getCount(Random rng) {
			return Mth.nextInt(rng, min, max);
		}

		@Override
		public RangeSerializer<UniformRange> getSerializer() {
			return SERIALIZER;
		}

		static class Serializer implements RangeSerializer<UniformRange> {
			@Override
			public void toJson(JsonObject jsonObject, UniformRange range) {
				jsonObject.addProperty("min", range.min);
				jsonObject.addProperty("max", range.max);
			}

			@Override
			public UniformRange fromJson(JsonObject jsonObject) {
				return new UniformRange(GsonHelper.getAsInt(jsonObject, "min"), GsonHelper.getAsInt(jsonObject, "max"));
			}

			@Override
			public void toNetwork(FriendlyByteBuf buffer, UniformRange range) {
				buffer.writeVarInt(range.min);
				buffer.writeVarInt(range.max);
			}

			@Override
			public UniformRange fromNetwork(FriendlyByteBuf buffer) {
				int min1 = buffer.readVarInt();
				int max1 = buffer.readVarInt();
				return new UniformRange(min1, max1);
			}
		}

	}

	record BinomialRange(int n, float p) implements ItemCountRange {

		public static final Serializer SERIALIZER = new Serializer();

		@Override
		public int getCount(Random rng) {
			int v = 0;
			for (int i = 0; i < n; i++) {
				if (rng.nextFloat() < p) v++;
			}
			return v;
		}

		@Override
		public RangeSerializer<BinomialRange> getSerializer() {
			return SERIALIZER;
		}

		static class Serializer implements RangeSerializer<BinomialRange> {

			@Override
			public void toJson(JsonObject jsonObject, BinomialRange range) {
				jsonObject.addProperty("n", range.n);
				jsonObject.addProperty("p", range.p);
			}

			@Override
			public BinomialRange fromJson(JsonObject jsonObject) {
				return new BinomialRange(GsonHelper.getAsInt(jsonObject, "n"), GsonHelper.getAsFloat(jsonObject, "p"));
			}

			@Override
			public void toNetwork(FriendlyByteBuf buffer, BinomialRange range) {
				buffer.writeVarInt(range.n);
				buffer.writeFloat(range.p);
			}

			@Override
			public BinomialRange fromNetwork(FriendlyByteBuf buffer) {
				int n1 = buffer.readVarInt();
				float p1 = buffer.readFloat();
				return new BinomialRange(n1, p1);
			}
		}

	}

	record ConstantValue(int value) implements ItemCountRange {

		public static final Serializer SERIALIZER = new Serializer();

		@Override
		public int getCount(Random rng) {
			return value;
		}

		@Override
		public RangeSerializer<ConstantValue> getSerializer() {
			return SERIALIZER;
		}

		static class Serializer implements RangeSerializer<ConstantValue> {
			@Override
			public void toJson(JsonObject jsonObject, ConstantValue constant) {
				jsonObject.addProperty("value", constant.value);
			}

			@Override
			public ConstantValue fromJson(JsonObject jsonObject) {
				return new ConstantValue(jsonObject.get("value").getAsInt());
			}

			@Override
			public void toNetwork(FriendlyByteBuf buffer, ConstantValue constant) {
				buffer.writeVarInt(constant.value);
			}

			@Override
			public ConstantValue fromNetwork(FriendlyByteBuf buffer) {
				int constantValue = buffer.readVarInt();
				return new ConstantValue(constantValue);
			}
		}

	}

}
