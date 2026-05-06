SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO';
 
-- 1. Veritabanını Oluşturma
/*!40000 DROP DATABASE IF EXISTS `MovieCritics`*/;
CREATE DATABASE `MovieCritics` DEFAULT CHARACTER SET utf8mb4;
USE `MovieCritics`;

-- Persons
CREATE TABLE Persons (
    PersonID INT PRIMARY KEY,
    FirstName VARCHAR(100) NOT NULL,
    LastName VARCHAR(100) NOT NULL,
    DateOfBirth DATE,
    Nationality VARCHAR(100)
);

-- Movies
CREATE TABLE Movies (
	MovieID INT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(255) NOT NULL,
    ReleaseDate DATE, 
    Language VARCHAR(100),
    CountryOfOrigin VARCHAR(100),
    Genre VARCHAR(100),
    DirectorId INT, -- Normlade VARCHAR(255)  ama Persona foreıgn key olabilir dediği için INT yaptım değiştirilebilir
    Watched BOOLEAN DEFAULT FALSE,
    LeadingActorId INT, -- Normlade VARCHAR(255)  ama Persona foreıgn key olabilir dediği için INT yaptım değiştirilebilir
    SupportingActorId INT, -- Normlade VARCHAR(255)  ama Persona foreıgn key olabilir dediği için INT yaptım değiştirilebilir
    About TEXT,
    Rating INT CHECK (Rating >= 1 AND Rating <= 10),
    Comments TEXT,
    Poster VARCHAR(255),
    ParentalRestriction BOOLEAN DEFAULT FALSE,
    
    -- İlişkilerin tanımlanması
    CONSTRAINT FK_Director FOREIGN KEY (DirectorId) REFERENCES Persons(PersonID),
    CONSTRAINT FK_LeadingActor FOREIGN KEY (LeadingActorId) REFERENCES Persons(PersonID),
    CONSTRAINT FK_SupportingActor FOREIGN KEY (SupportingActorId) REFERENCES Persons(PersonID)
);

-- Users
CREATE TABLE Users (
    UserId INT AUTO_INCREMENT PRIMARY KEY,
    Username VARCHAR(100) UNIQUE NOT NULL,
    Password VARCHAR(100) NOT NULL,
    UserType INT, -- 1: Mother, 2: Father vb. şeklinde kodlanabilir
    Email VARCHAR(255) UNIQUE
);




CREATE TABLE UserMovieInteractions (
    InteractionID INT AUTO_INCREMENT PRIMARY KEY,
    UserID        INT NOT NULL,	
    MovieID       INT NOT NULL,
    Rating        INT CHECK (Rating IS NULL OR (Rating >= 1 AND Rating <= 10)),
    Comment       TEXT,
    Watched       BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (UserID)  REFERENCES Users(UserId),
    FOREIGN KEY (MovieID) REFERENCES Movies(MovieID),
    UNIQUE KEY uq_user_movie (UserID, MovieID) 
);

-- Persons
INSERT INTO Persons (PersonID, FirstName, LastName, DateOfBirth, Nationality) VALUES
(1, 'Christopher', 'Nolan', '1970-07-30', 'British-American'),
(2, 'Cillian', 'Murphy', '1976-05-25', 'Irish'),
(3, 'Quentin', 'Tarantino', '1963-03-27', 'American'),
(4, 'Leonardo', 'DiCaprio', '1974-11-11', 'American'),
(5, 'Samuel L.', 'Jackson', '1948-12-21', 'American');


-- Movies
INSERT INTO Movies (MovieID, Title, ReleaseDate, Language, CountryOfOrigin, Genre, DirectorId, Watched, LeadingActorId, SupportingActorId, About, Rating, Comments, Poster, ParentalRestriction) VALUES
(101, 'Inception', '2010-07-16', 'English', 'USA', 'Sci-Fi', 1, TRUE, 4, 2, 'A thief who steals corporate secrets through dream-sharing technology.', 9, 'Amazing concept, must watch again.', 'inception_poster.jpg', FALSE),
(102, 'Pulp Fiction', '1994-10-14', 'English', 'USA', 'Crime', 3, TRUE, 5, 4, 'Intertwined stories from the Los Angeles criminal underworld.', 10, 'A cult masterpiece.', 'pulp_fiction.png', TRUE),
(103, 'Oppenheimer', '2023-07-21', 'English', 'USA', 'Biography', 1, FALSE, 2, 4, 'The story of J. Robert Oppenheimer, the father of the atomic bomb.', 8, 'Cinematography is outstanding.', 'oppenheimer.webp', TRUE),
(104, 'Django Unchained', '2012-12-25', 'English', 'USA', 'Western', 3, TRUE, 5, 4, 'A freed slave teams up with a German bounty hunter.', 9, 'Tarantino action at its finest.', 'django.jpg', TRUE),
(105, 'The Dark Knight', '2008-07-18', 'English', 'USA', 'Action', 1, TRUE, 2, 5, 'The epic battle between Batman and the Joker.', 10, 'The greatest superhero film ever made.', 'dark_knight.jpg', FALSE);

-- Users
INSERT INTO Users (UserId, Username, Password, UserType, Email) VALUES
(1, 'Ayse_Murt', 'Ayse123*', 1, 'ayse@email.com'),
(2, 'Mehmet_Murt', 'Mehmet88*', 2, 'mehmet@email.com'),
(3, 'Osman_Murt', '24070006009', 2, 'Osman@email.com'),
(4, 'Derya_Murt', 'deniz_star', 1, 'Derya@email.com'),
(5, 'kuzen_Murt', 'ali12345', 2, 'Kuzen@email.com');

-- UserMovieInteractions
INSERT INTO UserMovieInteractions (UserID, MovieID, Rating, Comment, Watched) VALUES
(2, 101, 9, 'Inception was brilliant, would watch again.', TRUE),
(3, 101, 7, 'Hard to follow but very good.', TRUE),
(2, 105, 10, 'Best Batman film by far.', TRUE),
(3, 105, 8, 'The Joker character was incredible.', FALSE),
(5, 101, 8, 'Very layered story, loved it.', TRUE),
(5, 105, 9, 'Great action sequences throughout.', TRUE);
