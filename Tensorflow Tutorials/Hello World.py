##
# TensorFlow program to display Hello World
from __future__ import division
from __future__ import absolute_import
from __future__ import print_function

## can't import Session on PyCharm

import tensorflow as tf

# Create tensor
hello = tf.strings.join(["Hello, World"])
# Launch session
sess = tf.compat.v1.Session()
print(sess.run(hello))