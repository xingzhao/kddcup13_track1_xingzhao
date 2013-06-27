
OS:
this project was done on windows 7 enterprise on my laptop

thirdParty:

it uses FICO model builder 7.3.  
the key steps in the model builder project are copied here into the feature dir and model dir.
 


train model: 

the model is trained using FICO model builder scorecard which is explained in the paper model/scorecard.pdf.  
the model details can be found in the model/model.html 
There are 12 variables in the model.  These 12 variables are generated using java and groovy script based on the raw data from kaggle.  the feature generatation code can be found in feature/*.  The training data is included as model/Train.csv.  The binning details can be found in model/autoBin.mb. 

Note: the model submited here is retrained using both train and valid data. 



prediction: 

to make prediction, we first generate the features for the test data.  This is done similarly as the train data.   Then use the trained model to predict scores for the test data (author-paper pairs).  For each author, sort the paperids based on the prediction scores.  Refer to model/predict.mb and model/submit.mb for details.  

reproduce:
The training data can be reproduced using the same logic in the feature generation code.  the training data    is also included for verification. 
 
the model and scores can be reproduced exactly using FICO model builder.  There are no random seeds involved in feature generatation or training.  

Without FICO model builder, the model can be trained approximately using logistic regression. For each variable bin, the user needs to create a 0/1 bin Variable.  A logistic regression model can be trained using all these bin variables as predictors.   This logistic regression model will produce similar scores as the model trained using FICO model builder scorecard.  The binned data is included as model/Train_binned.csv. 

Difference is expected because of the penalty(regulation) and constraints used in scorecard training.  However, penalty and constraints only lead to minor differences for this problem.  






work flows: 

d_train.mb:   prepare the train data initially. (d_valid.mb will prepare the test data)  
f_varGens.mb:  generate all features.
join.mb:     join all features into one dataset.  
autobin.mb:  generate the binnings for variables. 
modelTrain.mb:  train and save the scorecard model
predict.mb:  generate scores using saved model
submit.mb:  sort paperids based on scores, generate the submit file. 


