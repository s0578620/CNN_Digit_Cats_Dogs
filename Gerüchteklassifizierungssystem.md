### Gerüchteklassifizierungssystem

Ein Beitrag kann als Nicht-Gerücht, wahres Gerücht, falsches Gerücht oder unbestätigtes Gerücht klassifiziert werden.
Die vier Komponenten des Gerüchteklassifizierungssystems werden alle durch ein rekurrentes neuronales Netz (RNN) mit BiLSTM-Zellen und deren Verlustfunktionen (loss functions) erfüllt.

Der erste Schritt bei der Gerüchteklassifizierung ist die Gerüchteerkennung. Die Identifizierung von Gerüchten und Nicht-Gerüchten wurde üblicherweise als binäres Klassifizierungsproblem formuliert. Unter den zahlreichen Ansätzen gibt es drei Hauptkategorien: auf handgefertigten Merkmalen basierende Ansätze, propagationsbasierte Ansätze und Ansätze mit neuronalen Netzen.

Nachdem ein Gerücht identifiziert wurde, sollten alle verwandten Beiträge oder Sätze, die dieses Gerücht diskutieren, für die spätere Verarbeitung geclustert und andere nicht verwandte Beiträge herausgefiltert werden. Diese Aufgabe der Gerüchteverfolgung kann als binäres Klassifizierungsproblem formuliert werden, bei dem Beiträge als mit einem Gerücht verbunden oder nicht verbunden klassifiziert werden.

Sobald die Gerüchteverfolgungskomponente die mit einem Gerücht zusammenhängenden Beiträge geclustert hat, kennzeichnet die Komponente zur Klassifizierung der Haltung jeden einzelnen Beitrag anhand seiner Ausrichtung auf die Wahrhaftigkeit des Gerüchts. Ein Beitrag oder eine Antwort kann zum Beispiel als Zustimmung, Ablehnung, Kommentar oder Frage gekennzeichnet werden.

Die letzte Komponente der Gerüchteklassifizierung ist die Wahrheitswertklassifizierung, die den Wahrheitswert eines Gerüchts bestimmt, d.h. ein Gerücht kann wahr, falsch oder unbestätigt sein. Einige Arbeiten haben die Wahrheitswertklassifizierung auf eine binäre Klassifizierung beschränkt, d. h. ein Gerücht kann entweder wahr oder falsch sein. Die begonnene Forschung in dieser Richtung befasst sich nicht direkt mit der Wahrhaftigkeit von Gerüchten, sondern eher mit deren Glaubwürdigkeitswahrnehmung.

### Architektur

RNN ist ein Netzwerktyp, der durch rekurrente Verbindungen ein Gedächtnis bildet. In Feed Forward-Netzwerken sind die Eingänge unabhängig voneinander. In RNNs sind jedoch alle Eingänge miteinander verbunden. In rekurrenten neuronalen Netzen tritt jedoch das Problem des verschwindenden Gradienten auf. Ein Problem bei RNNs ist, dass während des Trainings die Komponenten des Gradientenvektors über lange Sequenzen exponentiell wachsen oder abfallen können. Dieses Problem mit explodierenden oder verschwindenden Gradienten macht es für das RNN-Modell schwierig, Korrelationen über lange Strecken in einer Sequenz zu lernen.

![Untitled](Sentiment,%20veracity%20and%20stance%20analysis%207ae86fe44c7d4adbacc44314ab398fb1/Untitled%202.png)

LSTM, um das Problem des verschwindenden Gradienten von RNN zu überwinden. Die LSTM-Einheit hat drei Gates: forget, input & output gate. 

- Das Forget-Gate f entscheidet, welche Informationen weggelassen werden sollen.
- Der Output-Wert liegt aufgrund der sigmoidalen Aktivierungsfunktion zwischen 0 und 1.
- Das Input-Gate i entscheidet, wie viele neue Informationen in der aktuellen Speicherzelle gespeichert werden sollen.
- Das Output-Gate o bestimmt den Ausgangswert der LSTM-Einheit.

![Untitled](Sentiment,%20veracity%20and%20stance%20analysis%207ae86fe44c7d4adbacc44314ab398fb1/Untitled%203.png)

In einem BiLSTM fließen die Informationen nicht nur von hinten nach vorne, sondern auch von vorne nach hinten. Ein bidirektionales LSTM besteht also aus einem Vorwärts- und einem Rückwärts-LSTM.

![Untitled](Sentiment,%20veracity%20and%20stance%20analysis%207ae86fe44c7d4adbacc44314ab398fb1/Untitled%204.png)