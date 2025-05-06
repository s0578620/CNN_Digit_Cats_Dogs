import keras
import numpy as np
import pandas as pd
from keras.callbacks import ReduceLROnPlateau
from keras.layers import BatchNormalization
from keras.layers import Conv2D, MaxPooling2D
from keras.layers import Dense, Flatten
from keras.models import Sequential
from keras.preprocessing.image import ImageDataGenerator
from keras.utils.np_utils import to_categorical
from sklearn.metrics import confusion_matrix
from sklearn.model_selection import train_test_split

import plot_diffusion

# from keras.datasets import mnist

train = pd.read_csv('./data/mnist_train.csv')
test = pd.read_csv('./data/mnist_test.csv')
# sub = pd.read_csv('../input/sample_submission.csv')
print("Data are Ready!!")
print(f"Training data size is {train.shape}\nTesting data size is {test.shape}")

X = train.drop(['label'], 1).values
y = train['label'].values
X = X / 255.0
X = X.reshape(-1, 28, 28, 1)
y = to_categorical(y)

print(f"Label size {y.shape}")

# Split the train and the validation set for the fitting
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.1, random_state=0)

datagen = ImageDataGenerator(
    featurewise_center=False,  # set input mean to 0 over the dataset
    samplewise_center=False,  # set each sample mean to 0
    featurewise_std_normalization=False,  # divide inputs by std of the dataset
    samplewise_std_normalization=False,  # divide each input by its std
    zca_whitening=False,  # apply ZCA whitening
    rotation_range=15,  # randomly rotate images in the range (degrees, 0 to 180)
    zoom_range=0.01,  # Randomly zoom image
    width_shift_range=0.1,  # randomly shift images horizontally (fraction of total width)
    height_shift_range=0.1,  # randomly shift images vertically (fraction of total height)
    horizontal_flip=False,  # randomly flip images
    vertical_flip=False)  # randomly flip images

# datagen.fit(X_train)
train_gen = datagen.flow(X_train, y_train, batch_size=128)
val_gen = datagen.flow(X_test, y_test, batch_size=128)
test_gen = datagen.flow(X_test, y_test, batch_size=128)

model = Sequential(
    layers=[
        Conv2D(filters=64, kernel_size=(3, 3), activation="relu", input_shape=(28, 28, 1)),
        Conv2D(filters=64, kernel_size=(3, 3), activation="relu"),
        MaxPooling2D(pool_size=(2, 2)),
        BatchNormalization(),
        Conv2D(filters=128, kernel_size=(3, 3), activation="relu"),
        Conv2D(filters=128, kernel_size=(3, 3), activation="relu"),
        MaxPooling2D(pool_size=(2, 2)),
        BatchNormalization(),
        Conv2D(filters=256, kernel_size=(3, 3), activation="relu"),
        MaxPooling2D(pool_size=(2, 2)),
        BatchNormalization(),
        Flatten(),
        Dense(512, activation="relu"),
        Dense(10, activation="softmax")
    ])

model.compile(loss="categorical_crossentropy", optimizer="adam", metrics=["accuracy"])
model.summary()

epochs = 10
batch_size = 128
train_steps = X_train.shape[0] // batch_size
valid_steps = X_test.shape[0] // batch_size

# es = keras.callbacks.EarlyStopping(
#     monitor="val_accuracy",  # metrics to monitor
#     patience=10,  # how many cm_plots before stop
#     verbose=1,
#     mode="max",
#     restore_best_weights=True,
# )
#
# rp = keras.callbacks.ReduceLROnPlateau(
#     monitor="val_accuracy",
#     factor=0.2,
#     patience=3,
#     verbose=1,
#     mode="max",
#     min_lr=0.00001,
# )


def run():
    for epoch in range(1, 2):
        _ = model.fit(train_gen,
                      epochs=1,
                      steps_per_epoch=train_steps,
                      validation_data=val_gen,
                      validation_steps=valid_steps,
                      # callbacks=[es, rp, tensorboard_callback, cm_callback]
                      )

        y_pred = model.predict(X_test)

        Y_pred = np.argmax(y_pred, 1)
        Y_test = np.argmax(y_test, 1)

        mat = confusion_matrix(Y_test, Y_pred, labels=[0, 1, 2, 3, 4, 5, 6, 7, 8, 9])

        # optional custom normalization
        # norm = np.vectorize(lambda x: 0 if x == 0 else x if x > 20 else -(x * 50))t

        plot_diffusion.as_heatmap(mat, epoch, dir="./epochs/")


if __name__ == "__main__":
    run()
