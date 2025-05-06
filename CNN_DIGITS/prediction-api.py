from io import StringIO
from os import listdir
from os.path import isfile, join

import pandas as pd
import tensorflow as tf
from fastapi import FastAPI
from matplotlib import pyplot as plt
from pydantic import BaseModel
from tensorflow import keras


class Model(BaseModel):
    num: str


class Image(BaseModel):
    data: str


class LoadedModel:
    model = None
    prediction = None
    img = None

    def __init__(self, path: str):
        self.model = keras.models.load_model(path)

    def predict(self, image_csv: str):
        img_val__ = pd.read_csv(StringIO(image_csv)).values / 255.0
        self.img = img_val__.reshape(-1, 28, 28, 1)
        prediction = self.model.predict(self.img)
        arg_list = prediction[0].tolist()
        print(arg_list)
        return {"table": arg_list}

    def features(self, save_file: str):
        i = 0
        input_img = self.img.reshape(1, 28, 28, 1)
        fig, axs = plt.subplots(nrows=8, ncols=8, figsize=(20, 20))
        for _, layer in enumerate(self.model.layers):
            if isinstance(layer, tf.keras.layers.Conv2D):
                i += 1
                get_feature_maps = tf.keras.backend \
                    .function([self.model.input], [layer.output])
                feature_maps = get_feature_maps([input_img])[0]
                n_filters = feature_maps.shape[3]
                for j in range(8):
                    plt_axs = axs[i, j]
                    # plt_axs.figure(figsize=(20, 20))
                    plt_axs.imshow(feature_maps[0, :, :, j], cmap="gray")
                    plt_axs.axis("off")
        # plt.show()
        plt.savefig(f"{save_file}")


class PredictionAPI(FastAPI):
    loaded_model: LoadedModel = None
    models: {} = None
    trained_models = "./data/trained_models"


app = PredictionAPI()


@app.get("/")
async def root():
    return 200


@app.get("/model")
async def show_models():
    app.models = \
        {i: m for i, m in enumerate(
            [f for f in listdir(app.trained_models) if
             isfile(join(app.trained_models, f))]
        )}
    return app.models


@app.post("/model")
async def select_model(model: Model):
    app.loaded_model = LoadedModel(
        join(app.trained_models, app.models[int(model.num)]))
    return 200 if app.loaded_model.model is not None else 500


@app.post("/predict")
async def predict_for_image(image: Image):
    loaded_model = app.loaded_model
    return loaded_model.predict(image.data) \
        if loaded_model is not None \
        else {"code": 500, "msg": "load a model first please!"}


@app.get("/features")
async def features():
    loaded_model = app.loaded_model
    loaded_model.features("./pics/features.png")
    return 200 \
        if loaded_model is not None \
        else {"code": 500, "msg": "load a model first please!"}


@app.get("/reset")
async def reset():
    app.loaded_model = None
    return "model was reset, please set one to predict again!"
