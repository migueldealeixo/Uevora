import numpy as np
import matplotlib.pyplot as plt
import pandas as pd

from sklearn.datasets import load_breast_cancer

from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsClassifier


#load data
cancer = load_breast_cancer()

#check data
print("cancer.keys(): \n{}".format(cancer.keys()))
print("Shape of cancer data: {}".format(cancer.data.shape))
print("Sample counts per class:\n{}".format(
	{n: v for n, v in zip(cancer.target_names, np.bincount(cancer.target))}))
print("Feature names:\n{}".format(cancer.feature_names))

#split data
X_train, X_test, y_train, y_test = train_test_split(
	cancer.data, cancer.target, stratify=cancer.target, random_state=66)

#build model
knn = KNeighborsClassifier(n_neighbors=1)
knn.fit(X_train, y_train)

#use model
pred_knn = knn.predict(X_test)
