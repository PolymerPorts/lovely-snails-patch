/*
 * Copyright (c) 2021 LambdAurora <email@lambdaurora.dev>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.pb4.lovelysnailspatch.impl.entity.model;

import dev.lambdaurora.lovely_snails.entity.SnailEntity;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.CubeConsumer;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.*;
import org.joml.Matrix4fStack;

import static eu.pb4.factorytools.api.virtualentity.emuvanilla.model.PartNames.*;


/**
 * Represents the snail entity model.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.0.0
 */
public class SnailModel extends EntityModel<SnailEntity> {
	public static final String SHELL = "shell";

	public static final float ADULT_SHELL_ROTATION = -0.0436f;
	private static final float ADULT_FRONT_WIDTH = 12.f;
	private static final float ADULT_SHELL_DIAMETER = 16.f;
	private static final float ADULT_EYE_YAW = 0.1745f;
	private static final float ADULT_EYE_LENGTH = 12.f;
	private static final float ADULT_EYE_DIAMETER = 2.f;

	public static final float BABY_SHELL_ROTATION = -0.080f;
	private static final float BABY_FRONT_WIDTH = 4.f;
	private static final float BABY_SHELL_DIAMETER = 10.f;
	private static final float BABY_EYE_YAW = ADULT_EYE_YAW;
	private static final float BABY_EYE_LENGTH = 7.f;
	private static final float BABY_EYE_DIAMETER = 1.f;

	private final Model adultModel;
	private final Model babyModel;
	private Model currentModel;

	public SnailModel(ModelPart root) {
        super(root);
        this.adultModel = new Model(root.getChild("adult"), ADULT_SHELL_ROTATION);
		this.babyModel = new Model(root.getChild("baby"), BABY_SHELL_ROTATION);
	}

	public static LayerDefinition model(CubeDeformation dilation) {
		var modelData = new MeshDefinition();
		var root = modelData.getRoot();
		buildAdultModel(root.addOrReplaceChild("adult", new CubeListBuilder(), PartPose.ZERO), dilation);
		buildBabyModel(root.addOrReplaceChild("baby", new CubeListBuilder(), PartPose.ZERO), dilation);
		return LayerDefinition.create(modelData, 128, 96);
	}

	private static void buildAdultModel(PartDefinition root, CubeDeformation dilation) {
		var body = root.addOrReplaceChild(BODY, new CubeListBuilder()
						.texOffs(0, 32)
						.addBox(-(ADULT_FRONT_WIDTH / 2.f), 5.f, -20.f, ADULT_FRONT_WIDTH, 3.f, 40.f, dilation),
				PartPose.offset(0.f, 16.f, -2.f));
		var upperBody = body.addOrReplaceChild("upper_body", new CubeListBuilder()
						.texOffs(64, 16)
						.addBox(-(ADULT_FRONT_WIDTH / 2.f), -7.f, 0.f, ADULT_FRONT_WIDTH, 12.f, 8.f,
								dilation),
				PartPose.offset(0.f, 0.f, -20.f));
		upperBody.addOrReplaceChild("left_tentacle", new CubeListBuilder()
						.texOffs(0, 2)
						.addBox(-ADULT_FRONT_WIDTH / 2.f, 0.f, -2.f, 4.f, 4.f, 2.f, dilation),
				PartPose.ZERO
		);
		upperBody.addOrReplaceChild("right_tentacle", new CubeListBuilder()
						.texOffs(0, 2)
						.mirror()
						.addBox(ADULT_FRONT_WIDTH / 2.f - 4.f, 0.f, -2.f, 4.f, 4.f, 2.f, dilation),
				PartPose.ZERO
		);

		root.addOrReplaceChild(SHELL, new CubeListBuilder()
						.addBox(-(ADULT_FRONT_WIDTH / 2.f), 0.f, -2.f, ADULT_FRONT_WIDTH, ADULT_SHELL_DIAMETER, ADULT_SHELL_DIAMETER,
								dilation.extend(4.f, 8.f, 8.f),
								1.f, 1.f),
				PartPose.offsetAndRotation(0.f, -2.f, -5.f, ADULT_SHELL_ROTATION, 0.f, 0.f));

		body.addOrReplaceChild(LEFT_EYE, new CubeListBuilder()
						.texOffs(42, 0)
						.addBox(-2.8336f, -15.849f, -3.8272f, ADULT_EYE_DIAMETER, ADULT_EYE_LENGTH, ADULT_EYE_DIAMETER, dilation),
				PartPose.offsetAndRotation(-1.5f, -4.f, -15f, 0.4363f, ADULT_EYE_YAW, 0.f));
		body.addOrReplaceChild(RIGHT_EYE, new CubeListBuilder()
						.texOffs(42, 0)
						.mirror()
						.addBox(0.8336f, -15.849f, -3.8272f, ADULT_EYE_DIAMETER, ADULT_EYE_LENGTH, ADULT_EYE_DIAMETER, dilation),
				PartPose.offsetAndRotation(1.5f, -4.f, -15f, 0.4363f, -ADULT_EYE_YAW, 0.f));
	}

	private static void buildBabyModel(PartDefinition babyRoot, CubeDeformation dilation) {
		var body = babyRoot.addOrReplaceChild(BODY, new CubeListBuilder()
						.texOffs(56, 0)
						.addBox(-(BABY_FRONT_WIDTH / 2.f), 22.f, -7.f, BABY_FRONT_WIDTH, 2.f, 14.f, dilation)
						.texOffs(0, 10)
						.addBox(-(BABY_FRONT_WIDTH / 2.f), 20.f, -7.f, BABY_FRONT_WIDTH, 2.f, 4.f, dilation)
						.texOffs(0, 0)
						.addBox(-(BABY_FRONT_WIDTH / 2.f), 22.f, -8.f, 1.f, 1.f, 1.f, dilation)
						.addBox(BABY_FRONT_WIDTH / 2.f - 1.f, 22.f, -8.f, 1.f, 1.f, 1.f, dilation),
				PartPose.offset(0, 0, -2.f));
		babyRoot.addOrReplaceChild(SHELL, new CubeListBuilder()
						.texOffs(0, 32)
						.addBox(-3.f, 10.f, -1.f, 6.f, BABY_SHELL_DIAMETER, BABY_SHELL_DIAMETER, dilation),
				PartPose.offsetAndRotation(0.f, 2.2f, -3.f, BABY_SHELL_ROTATION, 0.f, 0.f));
		body.addOrReplaceChild(LEFT_EYE, new CubeListBuilder()
						.texOffs(0, 32)
						.addBox(-1.1664f, 19.f, -3.8272f, BABY_EYE_DIAMETER, BABY_EYE_LENGTH, BABY_EYE_DIAMETER, dilation),
				PartPose.offsetAndRotation(-1.5f, -4.f, -14.2f, 0.4363f, BABY_EYE_YAW, 0.f));
		body.addOrReplaceChild(RIGHT_EYE, new CubeListBuilder()
						.texOffs(0, 32)
						.mirror()
						.addBox(0.1664f, 19.f, -3.8272f, BABY_EYE_DIAMETER, BABY_EYE_LENGTH, BABY_EYE_DIAMETER, dilation),
				PartPose.offsetAndRotation(1.5f, -4.f, -14.2f, 0.4363f, -BABY_EYE_YAW, 0.f));
	}

	public Model getCurrentModel(SnailEntity entity) {
		return entity.isBaby() ? this.babyModel : this.adultModel;
	}

	@Override
	public void setupAnim(SnailEntity entity) {
		var model = this.getCurrentModel(entity);
		this.currentModel = model;
		model.root.visible = true;
		(model == adultModel ? babyModel : adultModel).root.visible = false;

		if (entity.isScared()) model.hideSnail();
		else model.uncover();
	}

	@Override
	public void render(Matrix4fStack matrices, CubeConsumer vertices) {
		matrices.pushMatrix();
		this.babyModel.render(matrices, vertices);
		this.adultModel.render(matrices, vertices);
		matrices.popMatrix();
	}

	public static class Model {
		private final ModelPart root;
		private final ModelPart body;
		private final ModelPart shell;
		private final float idleShellYaw;

		public Model(ModelPart root, float idleShellYaw) {
			this.root = root;
			this.idleShellYaw = idleShellYaw;
			this.body = root.getChild(BODY);
			this.shell = root.getChild(SHELL);
		}

		/**
		 * Returns the shell of the snail.
		 *
		 * @return the shell
		 */
		public ModelPart getShell() {
			return this.shell;
		}

		/**
		 * Puts the snail in hiding.
		 */
		public void hideSnail() {
			this.body.visible = false;
			this.getShell().setRotation(0.f, 0.f, 0.f);
		}

		/**
		 * Puts the snail in idle position.
		 */
		public void uncover() {
			this.body.visible = true;
			this.getShell().setRotation(this.idleShellYaw, 0.f, 0.f);
		}

		public void render(Matrix4fStack matrices, CubeConsumer vertices) {
			if (!this.body.visible && this.root.visible) matrices.translate(0, 2.f / 16.f, 0);
			this.root.render(matrices, vertices);
		}
	}
}