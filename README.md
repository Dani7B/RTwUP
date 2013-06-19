#RTwUP

##Realtime Twitter Url Popularity

Dato un flusso di documenti provenienti da una query su Twitter ‘filter’ (e.g. tweet provenienti da Roma), realizzare delle statistiche aggiornate in tempo reale, che mostrino la classifica degli url più twittati da quando il sistema è attivo. Le statistiche vengono stampate a schermo ogni N secondi. 
Le statistiche mostrano i link organizzati in varie categorie di domini, ognuno con il suo conteggio di popolarità: 
foursquare.com: expanded.url.com/123 - 9 times; expanded.url.com/456 - 8 times
youtube.com: ...
instagram.com: ...
...
Other
All

###Descrizione del flusso dei dati: 
I Tweets sono scaricati a partire da una query con le Twitter APIs (e.g. coordinate di un poligono centrato su Roma, oppure Milano, etc... a scelta); 
I link trovati nel campo entities/urls del json del tweet sono quelli di interesse: 
se è già presente nel tweet la forma espansa dell’url, il conteggio viene assegnato allo stesso; 
se è presente una forma “shortened” (e.g. bit.ly/13NHE7v , goo.gl/uJH2Y , http://instagr.am/p/S3l5rQjCcA/ etc... ), allora è necessario espanderla, arrivando (eventualmente con diverse espansioni) alla forma finale completamente espansa. 
A partire dalla forma finale espansa, si può estrarre la categoria del dominio a cui questa appartiene, per organizzare i risultati attuali. 


###References: 
https://github.com/nathanmarz/storm/wiki

http://twitter4j.org/en/

https://github.com/twitter/hbc
