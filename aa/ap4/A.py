import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.datasets import load_breast_cancer
from sklearn.metrics import accuracy_score, classification_report
from sklearn.feature_selection import SelectFromModel

#Base model
data = load_breast_cancer()
X = data.data
y = data.target
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.25, random_state=3)
rf = RandomForestClassifier(random_state=42)
rf.fit(X_train, y_train)
train_predict = rf.predict(X_train)
test_predict = rf.predict(X_test)
testAccuracy = accuracy_score(y_test, test_predict)
print(f"BASEMODEL: ",testAccuracy)


for max_feat in [2, 5, 10, 'sqrt', 'log2']:
    rf = RandomForestClassifier(max_features=max_feat, random_state=42)
    rf.fit(X_train, y_train)
    test_pred = rf.predict(X_test)
    print(f"max_features={max_feat}, Accuracy: {accuracy_score(y_test, test_pred)}")

for depth in [2, 3, 5, 10, None]:
    rf = RandomForestClassifier(max_depth=depth, random_state=42)
    rf.fit(X_train, y_train)
    test_pred = rf.predict(X_test)
    print(f"max_depth={depth}, Accuracy: {accuracy_score(y_test, test_pred)}")
