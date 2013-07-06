#RTwUP

##Realtime Twitter Url Popularity

=======
Dato un flusso di documenti provenienti da una query su Twitter, filtrata in modo opportuno , realizzare delle statistiche aggiornate in tempo reale che mostrino la classifica degli URL più twittati da quando il sistema è attivo.
Le statistiche vengono stampate a schermo ogni N secondi.  
Le statistiche mostrano i link organizzati in varie categorie di domini, ognuno con il suo conteggio di popolarità:  
foursquare.com: expanded.url.com/123 - 9 times; expanded.url.com/456 - 8 times  
  youtube.com: ...  
	instagram.com: ...  
	...  
	Other  
	All  

###Descrizione del flusso dei dati: 
I Tweets sono scaricati a partire da una query con le Twitter APIs (ad esempio [Twitter4j][02], [Hosebird][03]), filtrate opportunamente (ad esempio, in base alle coordinate di un poligono centrato su Roma, oppure Milano, etc... a scelta).  
I link trovati nel campo entities/urls del json del tweet sono quelli di interesse: 
* se è già presente nel tweet la forma espansa dell’url, il conteggio viene assegnato allo stesso; 
* se è presente una forma “shortened” (ad esempio bit.ly/13NHE7v , goo.gl/uJH2Y , http://instagr.am/p/S3l5rQjCcA/, ecc ...), allora è necessario espanderla, arrivando (eventualmente con diverse espansioni) alla forma finale completamente espansa.
 
A partire dalla forma finale espansa, si può estrarre la categoria del dominio a cui questa appartiene, per organizzare i risultati attuali.
Tutto questo verrà effettuato in tempo reale, usando [Storm][01]


[01]: https://github.com/nathanmarz/storm/wiki "Wiki di Storm"

[02]: http://twitter4j.org/en/ "Sito di riferimento per le APIs di Twitter in Java"

[03]: https://github.com/twitter/hbc "Hosebird client"

##Wiki

* [Setting up the Maven project] (https://github.com/Dani7B/RTwUP/wiki/Setting-up-the-Maven-project)
* [Creation of a Twitter account to use] (https://github.com/Dani7B/RTwUP/wiki/Creation-of-a-Twitter-account-to-use)

##The story so far...
After properly setting up the project, we started working with Storm and created a Spout that retrieves all the tweets with links from Rome urban area. Test class is used to try methods without involving the Spout.

