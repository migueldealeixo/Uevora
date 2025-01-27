import numpy as np

from sklearn.datasets import load_iris

from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsClassifier
from sklearn.metrics import confusion_matrix


#load data
iris = load_iris()

#check data
print("iris.keys(): \n{}".format(iris.keys()))
print("Shape of iris data: {}".format(iris.data.shape))
print("Sample counts per class:\n{}".format(
	{n: v for n, v in zip(iris.target_names, np.bincount(iris.target))}))
print("Feature names:\n{}".format(iris.feature_names))

#split data
X_train, X_test, y_train, y_test = train_test_split(
	iris['data'], iris['target'], random_state=0)

#build model
knn = KNeighborsClassifier()
knn.fit(X_train, y_train)

#use model
pred_knn = knn.predict(X_test)

#matrix confusion
confusion = confusion_matrix(y_test, pred_knn)
print("Confusion matrix:\n{}".format(confusion))
print("Test set accuracy: {:.2f}".format(knn.score(X_test, y_test)))
