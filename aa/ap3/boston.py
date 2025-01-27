import numpy as np
import mglearn
import matplotlib.pyplot as plt

from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression
from sklearn.linear_model import Ridge
from sklearn.linear_model import Lasso


X,y = mglearn.datasets.load_extended_boston()
print("X.shape: {}".format(X.shape))

X_train, X_test, y_train, y_test = train_test_split(X, y, random_state=42)

alphas = [0,1,1,10]
coefs_ridge=[]
coefs_lasso = []

for alpha in alphas:
    ridge = Ridge(alpha = alpha)
    ridge.fit(X_train, y_train)
    coefs_ridge.append(ridge.coef_)

    lasso = Lasso(alpha=alpha)
    lasso.fit(X_train,y_train)
    coefs_lasso.append(lasso.coef_)

