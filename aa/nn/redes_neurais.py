from datetime import datetime
start_time = datetime.now()

from sklearn.datasets import load_digits
digits = load_digits()
digits.keys()
n_samples, n_features = digits.data.shape

print('Número de exemplos')
print(n_samples)
print('Número de atributos')
print(n_features)

print('Valores dos atributos do exemplo 1')
print(digits.data[0])
print("Forma de um exemplo: ", digits.data[0].shape)
print("Tipo de cada atributo: ", type(digits.data[0]))


print("Forma de uma imagem: ", digits.images[0].shape)
print("Tipo de dados de uma imagem: ", type(digits.images[0]))

import matplotlib.pyplot as plt
# set up the figure
fig = plt.figure(figsize=(6, 6))  # figure size in inches
fig.subplots_adjust(left=0, right=1, bottom=0, top=1, hspace=0.05, wspace=0.05)

# plot the digits: each image is 8x8 pixels
for i in range(144):
    ax = fig.add_subplot(12, 12, i + 1, xticks=[], yticks=[])
    ax.imshow(digits.images[i], cmap=plt.cm.binary, interpolation='nearest')
    
    # label the image with the target value
    #ax.text(0, 7, str(digits.target[i]))
plt.show()



from sklearn.model_selection import train_test_split
res = train_test_split(digits.data, digits.target, 
                       train_size=0.8,
                       test_size=0.2,
                       random_state=1)
train_data, test_data, train_labels, test_labels = res 


from sklearn.neural_network import MLPClassifier
mlp = MLPClassifier(hidden_layer_sizes=(5,),
                    activation='tanh',
                    alpha=1e-4,
                    solver='sgd',
                    tol=10e-5,
                    random_state=1,
                    learning_rate_init=.1,
                    verbose=True)
mlp.fit(train_data, train_labels)
test_predictions = mlp.predict(test_data)
train_predictions = mlp.predict(train_data)
test_predictions[:25] , test_labels[:25]


from sklearn.metrics import accuracy_score
from sklearn.metrics import classification_report,confusion_matrix
exatidao = accuracy_score(test_labels, test_predictions)
print()
print('Exatidão : {}'.format(exatidao))
print()
print('----------------- Relatório dados de treino -----------------')
print(classification_report(train_labels,train_predictions))
print('----------------- Relatório dados de teste ------------------')
print(classification_report(test_labels,test_predictions))
print()
end_time = datetime.now()
print('Tempo para correr esta experiência : {}'.format(end_time - start_time))
