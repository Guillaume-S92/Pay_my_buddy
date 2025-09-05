Script SQL BD Pay_My_Budy :

-- Table des utilisateurs
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Table des transactions
CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    description TEXT,
    amount DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES users(id),
    CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES users(id)
);

-- Table des connexions entre utilisateurs (relation N:N)
CREATE TABLE user_connections (
    user_id INT NOT NULL,
    connection_id INT NOT NULL,
    PRIMARY KEY (user_id, connection_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_connection FOREIGN KEY (connection_id) REFERENCES users(id)
);



💡 Notes importantes
La table user_connections permet d’enregistrer les relations d’amitié entre utilisateurs (comme dans un réseau social).

DECIMAL(10, 2) permet de gérer des montants financiers avec précision.

Les contraintes FOREIGN KEY assurent l'intégrité des liens entre les utilisateurs et les transactions.