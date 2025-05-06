# CNN (Convolutional Neural Networks)

CNNs are a type of artificial neural network used for working with images. A fundamental issue with using conventional neural networks on images is that number of independent weights required to connect two consecutive layers increases exponentially with input image sizes. A CNN on the other hand, uses convolutional layers, which are a set of many small kernels (filters) that are convolved with images.

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/88db21b8-3563-4e23-81df-748f684636c0/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20221018%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20221018T084724Z&X-Amz-Expires=86400&X-Amz-Signature=6b6213fba99fdcc2ec75f70353ae33fa39130f91fd501f336360c93529a94f61&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

![Untitled](https://s3.us-west-2.amazonaws.com/secure.notion-static.com/27aaef2d-7058-4969-bb8f-caa96f5f5d4c/Untitled.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAT73L2G45EIPT3X45%2F20221018%2Fus-west-2%2Fs3%2Faws4_request&X-Amz-Date=20221018T084759Z&X-Amz-Expires=86400&X-Amz-Signature=93f0e3dff2b05f2b4fc8601bac341c22b850f9d789e5cb720fcd82d3e1f9719e&X-Amz-SignedHeaders=host&response-content-disposition=filename%20%3D%22Untitled.png%22&x-id=GetObject)

A **Convolutional Neural Network (ConvNet/CNN)** is a Deep Learning algorithm which can take in an input image, assign importance (learnable weights and biases) to various aspects/objects in the image and be able to differentiate one from the other. The pre-processing required in a ConvNet is much lower as compared to other classification algorithms. While in primitive methods filters are hand-engineered, with enough training, ConvNets have the ability to learn these filters/characteristics.

The architecture of a ConvNet is analogous to that of the connectivity pattern of Neurons in the Human Brain and was inspired by the organization of the Visual Cortex. Individual neurons respond to stimuli only in a restricted region of the visual field known as the Receptive Field. A collection of such fields overlap to cover the entire visual area.

****Why ConvNets over Feed-Forward Neural Nets?****

An image is nothing but a matrix of pixel values, right? So why not just flatten the image (e.g. 3x3 image matrix into a 9x1 vector) and feed it to a Multi-Level Perceptron for classification purposes? Uh.. not really.

In cases of extremely basic binary images, the method might show an average precision score while performing prediction of classes but would have little to no accuracy when it comes to complex images having pixel dependencies throughout.

A ConvNet is able to **successfully capture the Spatial and Temporal dependencies** in an image through the application of relevant filters. The architecture performs a better fitting to the image dataset due to the reduction in the number of parameters involved and reusability of weights. In other words, the network can be trained to understand the sophistication of the image better.

![Convoluting a 5x5x1 image with a 3x3x1 kernel to get a 3x3x1 convolved feature](https://miro.medium.com/max/786/1*GcI7G-JLAQiEoCON7xFbhg.gif)

Convoluting a 5x5x1 image with a 3x3x1 kernel to get a 3x3x1 convolved feature

![Convolution operation on a MxNx3 image matrix with a 3x3x3 Kernel](https://miro.medium.com/max/1400/1*ciDgQEjViWLnCbmX-EeSrA.gif)

Convolution operation on a MxNx3 image matrix with a 3x3x3 Kernel

[A Comprehensive Guide to Convolutional Neural Networks - the ELI5 way](https://towardsdatascience.com/a-comprehensive-guide-to-convolutional-neural-networks-the-eli5-way-3bd2b1164a53)

[VGG-16 | CNN model - GeeksforGeeks](https://www.geeksforgeeks.org/vgg-16-cnn-model/)