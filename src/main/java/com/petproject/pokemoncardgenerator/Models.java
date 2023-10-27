package com.petproject.pokemoncardgenerator;

public class Models {

	private Models() {
	}

	public static final String TEXT_GENERATION_MODEL = "mistralai/Mistral-7B-Instruct-v0.1";

	// base style levtech/siennatest5
	// old style pokemon dbecker1/sd-pokemon-model-lora-sdxl
	// 3D cartoon style goofyai/3d_render_style_xl
	// leonardo AI style goofyai/Leonardo_Ai_Style_Illustration
	// anime style Linaqruf/animagine-xl
	// stable difussion XL 1 stabilityai/stable-diffusion-xl-base-1.0
	public static final String IMAGE_GENERATION_MODEL = "levtech/siennatest5";

}
