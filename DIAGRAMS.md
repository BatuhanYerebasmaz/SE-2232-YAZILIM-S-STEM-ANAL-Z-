# MovieCritics System — UML & DFD Diagrams

> **Stack:** Java 17 · Swing (Nimbus LAF) · MySQL 8 · JDBC (`com.mysql.cj.jdbc.Driver`)  
> **Database:** `MovieCritics` — tables: `Persons`, `Movies`, `Users`, `UserMovieInteractions`

---

## 1. Use Case Diagram

```plantuml
@startuml UC_MovieCritics
left to right direction
skinparam actorStyle awesome
skinparam usecase {
  BackgroundColor #1F1F1F
  BorderColor #E50914
  FontColor #FFFFFF
  ArrowColor #B3B3B3
}

actor "Adult User\n(UserType=1)" as Adult
actor "Child User\n(UserType=2)" as Child
actor "MySQL Database" as DB #lightgrey

rectangle "MovieCritics System" {

  '-- Auth --
  usecase "UC01\nLogin" as UC_Login
  usecase "UC02\nValidate Credentials\n[DB lookup]" as UC_Validate

  '-- Shared browsing --
  usecase "UC03\nBrowse Movie Catalogue" as UC_Browse
  usecase "UC04\nSearch by Title /\nLanguage / Year" as UC_Search
  usecase "UC05\nFilter by Genre" as UC_Filter
  usecase "UC06\nView Movie Detail\n[double-click]" as UC_Detail

  '-- Shared interactions --
  usecase "UC07\nMark as Watched" as UC_Watch
  usecase "UC08\nRate Movie\n[1–10]" as UC_Rate
  usecase "UC09\nAdd Comment" as UC_Comment
  usecase "UC10\nAdd to Watchlist" as UC_WLAdd
  usecase "UC11\nRemove from Watchlist" as UC_WLRemove
  usecase "UC12\nView My Watchlist" as UC_WLView
  usecase "UC13\nView My Progress\n[watched count vs total]" as UC_Progress
  usecase "UC14\nView Family Ratings\n[approved only]" as UC_Family

  '-- Shared sub-use-case --
  usecase "UC15\nUpsert Interaction\n[INSERT or UPDATE\nUserMovieInteractions]" as UC_Upsert

  '-- Adult-only --
  usecase "UC16\nAdd Movie" as UC_Add
  usecase "UC17\nEdit Movie" as UC_Edit
  usecase "UC18\nDelete Movie" as UC_Delete
  usecase "UC19\nSet Parental Restriction\n[toggle boolean]" as UC_Restrict
  usecase "UC20\nManage Users\n[add/delete/reset pw]" as UC_Users
  usecase "UC21\nModerate Comments\n[approve/reject]" as UC_Moderate
  usecase "UC22\nView Analytics\n[totals & avg rating]" as UC_Analytics
  usecase "UC23\nLoad Person Combos\n[Director/Actor]" as UC_Persons

  '--- <<include>> chains ---
  UC_Login    ..> UC_Validate   : <<include>>

  UC_Browse   ..> UC_Login      : <<include>>
  UC_Watch    ..> UC_Login      : <<include>>
  UC_Rate     ..> UC_Login      : <<include>>
  UC_Comment  ..> UC_Login      : <<include>>
  UC_WLAdd    ..> UC_Login      : <<include>>
  UC_WLRemove ..> UC_Login      : <<include>>
  UC_WLView   ..> UC_Login      : <<include>>
  UC_Progress ..> UC_Login      : <<include>>
  UC_Family   ..> UC_Login      : <<include>>
  UC_Add      ..> UC_Login      : <<include>>
  UC_Edit     ..> UC_Login      : <<include>>
  UC_Delete   ..> UC_Login      : <<include>>
  UC_Restrict ..> UC_Login      : <<include>>
  UC_Users    ..> UC_Login      : <<include>>
  UC_Moderate ..> UC_Login      : <<include>>
  UC_Analytics ..> UC_Login     : <<include>>

  UC_Watch    ..> UC_Upsert     : <<include>>
  UC_Rate     ..> UC_Upsert     : <<include>>
  UC_Comment  ..> UC_Upsert     : <<include>>
  UC_WLAdd    ..> UC_Upsert     : <<include>>
  UC_WLRemove ..> UC_Upsert     : <<include>>

  UC_Add      ..> UC_Persons    : <<include>>
  UC_Edit     ..> UC_Persons    : <<include>>

  '--- <<extend>> (optional behaviour) ---
  UC_Search   ..> UC_Browse     : <<extend>>\n[keyword entered]
  UC_Filter   ..> UC_Browse     : <<extend>>\n[genre selected]
  UC_Detail   ..> UC_Browse     : <<extend>>\n[double-click card]
  UC_Restrict ..> UC_Edit       : <<extend>>\n[toggle restriction]
  UC_Family   ..> UC_Detail     : <<extend>>\n[status = approved]

  '--- Actor associations ---
  Adult --> UC_Browse
  Adult --> UC_Search
  Adult --> UC_Filter
  Adult --> UC_Detail
  Adult --> UC_Watch
  Adult --> UC_Rate
  Adult --> UC_Comment
  Adult --> UC_WLAdd
  Adult --> UC_WLRemove
  Adult --> UC_WLView
  Adult --> UC_Progress
  Adult --> UC_Family
  Adult --> UC_Add
  Adult --> UC_Edit
  Adult --> UC_Delete
  Adult --> UC_Restrict
  Adult --> UC_Users
  Adult --> UC_Moderate
  Adult --> UC_Analytics

  Child --> UC_Browse
  Child --> UC_Search
  Child --> UC_Filter
  Child --> UC_Detail
  Child --> UC_Watch
  Child --> UC_Rate
  Child --> UC_Comment
  Child --> UC_WLAdd
  Child --> UC_WLRemove
  Child --> UC_WLView
  Child --> UC_Progress
  Child --> UC_Family

  UC_Validate --> DB
  UC_Upsert   --> DB
}
@enduml
```

---

## 2. Class Diagram

```plantuml
@startuml CD_MovieCritics
skinparam classAttributeIconSize 0
skinparam linetype ortho
skinparam class {
  BackgroundColor #1F1F1F
  BorderColor #E50914
  FontColor #FFFFFF
  ArrowColor #B3B3B3
}

abstract class JFrame <<javax.swing>>
abstract class JPanel <<javax.swing>>

'─────────────────────────────
' View layer
'─────────────────────────────
class LoginPage {
  - NameTextField    : JTextField
  - PasswordTextField: JPasswordField
  - LoginText        : JLabel
  - InvisibleLabel   : JLabel
  - Button           : JButton
  --
  + LoginPage()
  - ButtonActionPerformed(evt : ActionEvent) : void
  ' Reads Username+Password → SELECT Users WHERE Username=? AND Password=?
  ' Routes to AdultFrame (type=1) or ChildFrame (type=2)
}

class AdultFrame {
  - userId            : int
  - selectedMovieId   : int = -1
  - selectedCardPanel : JPanel
  --
  + AdultFrame(userId : int)
  - loadGenres()      : void
  ' SELECT DISTINCT Genre FROM Movies ORDER BY Genre
  - loadMovieCards()  : void
  ' SELECT m.*,CONCAT(p.FirstName,' ',p.LastName) AS LeadActor
  ' FROM Movies m LEFT JOIN Persons p ON m.LeadingActorId=p.PersonID
  ' optional WHERE Title/Language/Year LIKE keyword, Genre=?
  - showMovieDetail(movieId : int)  : void
  ' JOIN Persons d/la/sa ON DirectorId/LeadingActorId/SupportingActorId
  - deleteSelectedMovie()           : void
  ' DELETE FROM Movies WHERE MovieID=?
  - toggleParentalRestriction()     : void
  ' UPDATE Movies SET ParentalRestriction=NOT ParentalRestriction WHERE MovieID=?
  - showAnalytics()                 : void
  ' COUNT(*) total, COUNT DISTINCT watched, AVG(Rating), TOP rated movie
  - showFamilyRatings(movieId : int): void
  ' SELECT u.Username,i.Rating,i.Comment WHERE Status='approved'
}

class ChildFrame {
  - userId            : int
  - selectedMovieId   : int = -1
  - selectedCardPanel : JPanel
  --
  + ChildFrame(userId : int)
  - loadGenres()      : void
  ' SELECT DISTINCT Genre WHERE ParentalRestriction=FALSE
  - loadMovieCards()  : void
  ' WHERE ParentalRestriction=FALSE [+ keyword + genre filters]
  - showMovieDetail(movieId : int)      : void
  - upsertInteraction(movieId : int,\n    field : String, value : Object) : void
  ' SELECT InteractionID → UPDATE or INSERT UserMovieInteractions
  ' Always sets Status='pending'
  - showWatchlist()   : void
  ' SELECT Movies JOIN Interactions WHERE Watchlist=TRUE
  - showProgress()    : void
  ' COUNT available, COUNT watched by user, family leaderboard
  - showFamilyRatings(movieId : int)    : void
  ' Status='approved' only
}

class AddMovieForm {
  - parentFrame         : Frame
  - TitleTextField      : JTextField
  - LanguageTextField   : JTextField
  - ReleaseDateTextField: JTextField
  - RatingTextField     : JTextField
  - CountryTextField    : JTextField
  - PosterTextField     : JTextField
  - aboutArea           : JTextArea
  - commentsArea        : JTextArea
  - genreCombo          : JComboBox<String>
  - directorCombo       : JComboBox<String>
  - leadingActorCombo   : JComboBox<String>
  - supportingActorCombo: JComboBox<String>
  - restrictionCheckBox : JCheckBox
  --
  + AddMovieForm(parent : Frame)
  - loadPersonsToCombo() : void
  ' SELECT PersonID,CONCAT(FirstName,' ',LastName) FROM Persons
  - SaveButtonActionPerformed(evt : ActionEvent) : void
  ' INSERT INTO Movies(Title,ReleaseDate,Language,CountryOfOrigin,
  '   Genre,DirectorId,LeadingActorId,SupportingActorId,
  '   About,Rating,Comments,Poster,ParentalRestriction)
}

class EditMovieForm {
  - movieId              : int
  - parentFrame          : Frame
  ' Same fields as AddMovieForm
  --
  + EditMovieForm(movieId : int, parent : Frame)
  - loadMovieData()      : void
  ' SELECT m.*,p.PersonID aliased director/lead/support
  ' FROM Movies m LEFT JOIN Persons x3
  - SaveButtonActionPerformed(evt : ActionEvent) : void
  ' UPDATE Movies SET Title=?,ReleaseDate=?,... WHERE MovieID=?
  - loadPersonsToCombo() : void
}

class ModerationFrame {
  - parentFrame : Frame
  - jTable1     : JTable
  --
  + ModerationFrame(parent : Frame)
  - loadInteractions() : void
  ' SELECT i.InteractionID,u.Username,m.Title,i.Rating,i.Comment,i.Status
  ' FROM UserMovieInteractions i JOIN Users JOIN Movies
  ' ORDER BY FIELD(i.Status,'pending','approved','rejected')
  - updateStatus(newStatus : String) : void
  ' UPDATE UserMovieInteractions SET Status=? WHERE InteractionID=?
}

class ManageUsersFrame {
  - parentFrame : Frame
  - jTable1     : JTable
  --
  + ManageUsersFrame(parent : Frame)
  - loadUsers()         : void
  ' SELECT UserId,Username,Email,UserType FROM Users
  - addUser()           : void
  ' INSERT INTO Users(Username,Password,UserType,Email)
  - deleteUser()        : void
  ' DELETE FROM Users WHERE UserId=?
  - resetPassword()     : void
  ' UPDATE Users SET Password=? WHERE UserId=?
}

'─────────────────────────────
' UI Component
'─────────────────────────────
class Movie <<JPanel card>> {
  - movieId   : int
  --
  + Movie(id : int, title : String,\n    poster : String, rating : int,\n    leadActor : String,\n    restricted : boolean,\n    genre : String)
  + getMovieId() : int
  ' Loads poster from posters/<filename>
  ' Shows RESTRICTED (red) or FAMILY (grey) badge
}

'─────────────────────────────
' Infrastructure
'─────────────────────────────
class DatabaseConnection {
  - URL      : String = "jdbc:mysql://localhost:3306/MovieCritics" {static}
  - USER     : String = "root" {static}
  - PASSWORD : String {static}
  --
  + connect() : Connection {static}
  ' Class.forName("com.mysql.cj.jdbc.Driver")
  ' DriverManager.getConnection(URL, USER, PASSWORD)
}

class Theme {
  + BG       : Color = #141414 {static}
  + SURFACE  : Color = #1F1F1F {static}
  + SURFACE2 : Color = #2F2F2F {static}
  + RED      : Color = #E50914 {static}
  + TEXT     : Color = #FFFFFF {static}
  + TEXT_DIM : Color = #B3B3B3 {static}
  --
  + apply(container : Container)    : void {static}
  ' Recursively styles JLabel/JButton/JTextField
  ' JComboBox/JScrollPane(JTable|JTextArea)/JPanel
  + applyToFrame(frame : JFrame)    : void {static}
  ' Sets 1280×720, centres, calls apply()
  + applyToSub(frame : JFrame)      : void {static}
  ' No resize — for modal sub-windows
  + styleCard(card : JPanel)        : void {static}
  + selectCard(card : JPanel)       : void {static}
  ' RED border 2px
  + deselectCard(card : JPanel)     : void {static}
  - styleTable(table : JTable)      : void {static}
  ' Custom DefaultTableCellRenderer for cells AND header
  ' Nimbus LAF ignores setBackground — renderer is required
}

enum Genre {
  ACTION
  ADVENTURE
  ANIMATION
  COMEDY
  CRIME
  DOCUMENTARY
  DRAMA
  FANTASY
  HORROR
  MUSICAL
  MYSTERY
  ROMANCE
  SCIENCE_FICTION
  THRILLER
  WAR
  WESTERN
  --
  - displayName : String
  --
  + getDisplayName() : String
  + fromString(value : String) : Genre {static}
  + toString() : String
}

'─────────────────────────────
' Inheritance
'─────────────────────────────
JFrame <|-- LoginPage
JFrame <|-- AdultFrame
JFrame <|-- ChildFrame
JFrame <|-- AddMovieForm
JFrame <|-- EditMovieForm
JFrame <|-- ModerationFrame
JFrame <|-- ManageUsersFrame
JPanel <|-- Movie

'─────────────────────────────
' Dependencies & associations
'─────────────────────────────
LoginPage    ..>  DatabaseConnection : <<use>>
LoginPage    ..>  AdultFrame         : <<create>>\n[userType=1]
LoginPage    ..>  ChildFrame         : <<create>>\n[userType=2]

AdultFrame   ..>  DatabaseConnection : <<use>>
AdultFrame   ..>  AddMovieForm       : <<create>>
AdultFrame   ..>  EditMovieForm      : <<create>>
AdultFrame   ..>  ModerationFrame    : <<create>>
AdultFrame   ..>  ManageUsersFrame   : <<create>>
AdultFrame   "1" o-- "0..*" Movie   : contains

ChildFrame   ..>  DatabaseConnection : <<use>>
ChildFrame   "1" o-- "0..*" Movie   : contains

AddMovieForm ..>  DatabaseConnection : <<use>>
AddMovieForm ..>  Genre              : <<use>>

EditMovieForm ..> DatabaseConnection : <<use>>
EditMovieForm ..> Genre              : <<use>>

ModerationFrame  ..> DatabaseConnection : <<use>>
ManageUsersFrame ..> DatabaseConnection : <<use>>

AdultFrame   ..> Theme : <<use>>
ChildFrame   ..> Theme : <<use>>
LoginPage    ..> Theme : <<use>>
ModerationFrame  ..> Theme : <<use>>
ManageUsersFrame ..> Theme : <<use>>
Movie        ..> Theme : <<use>>
@enduml
```

---

## 3. Sequence Diagram — Authentication & Routing

```plantuml
@startuml SEQ_Login
skinparam sequenceArrowThickness 2
skinparam sequenceParticipantBorderColor #E50914

actor       "User"            as U
participant "LoginPage"       as LP
participant "DatabaseConnection" as DC
database    "MySQL\nMovieCritics" as DB
participant "AdultFrame"      as AF
participant "ChildFrame"      as CF

U  -> LP : launch application\nnew LoginPage().setVisible(true)
activate LP
LP -> DC : connect()
activate DC
DC -> DB : DriverManager.getConnection\n(jdbc:mysql://localhost:3306/MovieCritics)
activate DB
DB --> DC : Connection object
deactivate DB
DC --> LP : Connection (or null + JOptionPane error)
deactivate DC

U  -> LP : type username & password
U  -> LP : click "Enter" [ButtonActionPerformed]

LP -> DC : connect()
activate DC
DC --> LP : Connection
deactivate DC

LP -> DB : SELECT * FROM Users\nWHERE Username=? AND Password=?
activate DB

alt Credentials VALID
  DB --> LP : ResultSet {UserId, UserType, ...}
  deactivate DB
  LP -> LP : InvisibleLabel = "Login Successful!" (GREEN)

  alt UserType == 1  [Adult]
    LP -> AF : new AdultFrame(userId)
    activate AF
    AF -> AF : initComponents()
    AF -> AF : Theme.applyToFrame(this)\n[sets 1280×720, dark palette]
    AF -> DC : connect()
    activate DC
    DC --> AF : Connection
    deactivate DC
    AF -> DB : SELECT DISTINCT Genre\nFROM Movies ORDER BY Genre
    activate DB
    DB --> AF : genre list
    deactivate DB
    AF -> AF : loadGenres() → GenreFilterCombo populated
    AF -> DB : SELECT m.*, CONCAT(p.FirstName,' ',p.LastName)\nAS LeadActor FROM Movies m\nLEFT JOIN Persons p ON m.LeadingActorId=p.PersonID
    activate DB
    DB --> AF : full movie rows
    deactivate DB
    AF -> AF : loadMovieCards()\n→ creates Movie JPanel cards in 3-col grid
    AF --> U  : Adult dashboard visible
  else UserType == 2  [Child]
    LP -> CF : new ChildFrame(userId)
    activate CF
    CF -> CF : initComponents() + Theme.applyToFrame()
    CF -> DB : SELECT DISTINCT Genre FROM Movies\nWHERE ParentalRestriction = FALSE
    activate DB
    DB --> CF : filtered genre list
    deactivate DB
    CF -> DB : SELECT m.* FROM Movies m\nLEFT JOIN Persons p ...\nWHERE m.ParentalRestriction = FALSE
    activate DB
    DB --> CF : family-safe movie rows
    deactivate DB
    CF -> CF : loadMovieCards()
    CF --> U  : Child dashboard visible\n[no restricted movies shown]
    deactivate CF
  end

  LP -> LP : dispose()
  deactivate LP

else Credentials INVALID
  DB --> LP : empty ResultSet
  deactivate DB
  LP -> LP : InvisibleLabel = "Wrong password or name" (RED)
  LP --> U  : show error — login page stays open
end
@enduml
```

---

## 4. Sequence Diagram — User Movie Interaction (Rate / Comment / Watch / Watchlist)

```plantuml
@startuml SEQ_Interact
skinparam sequenceArrowThickness 2

actor "User\n(Adult or Child)" as U
participant "AdultFrame /\nChildFrame"  as F
participant "DatabaseConnection"        as DC
database    "UserMovieInteractions"     as UMI
database    "Movies"                    as MOV

== Select a movie card ==
U -> F  : click Movie card [mouseClicked]
F -> F  : if selectedCardPanel != null\n  Theme.deselectCard(prev)
F -> F  : selectedMovieId = card.getMovieId()
F -> F  : Theme.selectCard(card)\n[RED border 2px]

opt Double-click → show detail
  U -> F : double-click card
  F -> DC : connect()
  activate DC
  DC --> F : Connection
  deactivate DC
  F -> MOV : SELECT m.*, YEAR(m.ReleaseDate),\nCONCAT(d/la/sa) AS Director/Lead/Support\nFROM Movies m LEFT JOIN Persons x3\nWHERE m.MovieID = ?
  activate MOV
  MOV --> F : full detail row
  deactivate MOV
  F --> U : JOptionPane with Title/Year/Genre\nLanguage/Country/Director\nLead/Support/Rating/Watched/Comments
end

== Interaction buttons ==

alt [Mark Watched]
  U -> F : click MarkWatchedButton
  F -> F : guard: selectedMovieId == -1 → show "Select a movie first!"
  F -> F : upsertInteraction(movieId, "Watched", true)
  F -> DC : connect()
  activate DC
  DC --> F : Connection
  deactivate DC
  F -> UMI : SELECT InteractionID\nWHERE UserID=? AND MovieID=?
  activate UMI
  alt row EXISTS
    UMI --> F : InteractionID found
    deactivate UMI
    F -> UMI : UPDATE UserMovieInteractions\nSET Watched=TRUE, Status='pending'\nWHERE UserID=? AND MovieID=?
    activate UMI
    UMI --> F : 1 row updated
    deactivate UMI
  else NO row
    UMI --> F : empty
    deactivate UMI
    F -> UMI : INSERT INTO UserMovieInteractions\n(UserID, MovieID, Watched, Status)\nVALUES (?,?,TRUE,'pending')
    activate UMI
    UMI --> F : 1 row inserted
    deactivate UMI
  end
  F --> U : JOptionPane "Marked as watched!"
  F -> F  : loadMovieCards() [refresh grid]

else [Rate Movie]
  U -> F : click RateMovieButton
  F -> F : guard: selectedMovieId == -1 → show warning
  F -> U : JOptionPane.showInputDialog "Enter rating (1-10):"
  U -> F : enters score string

  opt user cancels
    F --> U : no action
  end

  F -> F : score = Integer.parseInt(input)
  alt score < 1 OR score > 10
    F --> U : JOptionPane "Rating must be between 1-10!"
  else valid score
    F -> F  : upsertInteraction(movieId, "Rating", score)
    F -> UMI : SELECT / UPDATE or INSERT\n[same upsert as above, field="Rating"]
    activate UMI
    UMI --> F : ok
    deactivate UMI
    F --> U : JOptionPane "Rating pending approval"
  end

else [Add Comment]
  U -> F : click AddCommentButton
  F -> F : guard: selectedMovieId == -1 → show warning
  F -> U : JOptionPane.showInputDialog "Enter your comment:"
  U -> F : enters text string
  opt user cancels or empty
    F --> U : no action
  end
  F -> F  : upsertInteraction(movieId, "Comment", text.trim())
  F -> UMI : SELECT / UPDATE or INSERT\n[field="Comment"]
  activate UMI
  UMI --> F : ok
  deactivate UMI
  F --> U : JOptionPane "Comment pending approval"

else [Add to Watchlist]
  U -> F : click AddWatchlistButton
  F -> F  : upsertInteraction(movieId, "Watchlist", true)
  F -> UMI : SELECT / UPDATE or INSERT\n[field="Watchlist", value=TRUE]
  activate UMI
  UMI --> F : ok
  deactivate UMI
  F --> U : JOptionPane "Added to watchlist!"

else [Remove from Watchlist]
  U -> F : click RemoveWatchlistButton
  F -> F  : upsertInteraction(movieId, "Watchlist", false)
  F -> UMI : UPDATE Watchlist=FALSE\nWHERE UserID=? AND MovieID=?
  activate UMI
  UMI --> F : ok
  deactivate UMI
  F --> U : JOptionPane "Removed from watchlist!"
end

== Progress / Watchlist views ==

alt [My Watchlist]
  U -> F : click WatchlistButton
  F -> DC : connect()
  activate DC
  DC --> F : Connection
  deactivate DC
  F -> UMI : SELECT m.Title, m.Genre, m.Rating\nFROM Movies m JOIN UserMovieInteractions i\nON m.MovieID=i.MovieID\nWHERE i.UserID=? AND i.Watchlist=TRUE\nORDER BY m.Title
  activate UMI
  UMI --> F : list rows
  deactivate UMI
  F --> U : JOptionPane formatted watchlist

else [My Progress]
  U -> F : click ProgressButton
  F -> DC : connect()
  activate DC
  DC --> F : Connection
  deactivate DC
  F -> MOV : COUNT(*) WHERE ParentalRestriction=FALSE
  activate MOV
  MOV --> F : total available
  deactivate MOV
  F -> UMI : COUNT(*) WHERE UserID=? AND Watched=TRUE
  activate UMI
  UMI --> F : myWatched
  deactivate UMI
  F -> UMI : SELECT u.Username, COUNT(i.InteractionID)\nFROM Users u LEFT JOIN Interactions i\nWHERE u.UserType=2 GROUP BY u.UserId\nORDER BY watched DESC
  activate UMI
  UMI --> F : family leaderboard
  deactivate UMI
  F --> U : JOptionPane with total / watched / remaining\n+ family leaderboard
end
@enduml
```

---

## 5. Sequence Diagram — Admin Movie Management (Add / Edit / Delete)

```plantuml
@startuml SEQ_Admin
skinparam sequenceArrowThickness 2

actor "Admin\n(Adult User)" as A
participant "AdultFrame"    as AF
participant "AddMovieForm"  as AMF
participant "EditMovieForm" as EMF
participant "DatabaseConnection" as DC
database    "Movies"  as MOV
database    "Persons" as PER

== Add Movie ==
A -> AF : click AddMovieButton
AF -> AF : this.setEnabled(false)
AF -> AMF : new AddMovieForm(this)
activate AMF
AMF -> AMF : initComponents()
AMF -> AMF : genreCombo.setModel(\n  new DefaultComboBoxModel<>()\n  populated from Genre.values())
AMF -> DC : connect()
activate DC
DC --> AMF : Connection
deactivate DC
AMF -> PER : SELECT PersonID,\nCONCAT(FirstName,' ',LastName)\nFROM Persons ORDER BY LastName
activate PER
PER --> AMF : person rows
deactivate PER
AMF -> AMF : loadPersonsToCombo()\n→ directorCombo / leadingActorCombo\n  / supportingActorCombo populated
AMF -> AMF : Theme.applyToSub(this)
AMF --> A  : AddMovieForm visible

A -> AMF : fill Title, Genre, Language, ReleaseDate\nRating, Country, Poster, About,\nComments, DirectorId, LeadActor,\nSupportActor, ParentalRestriction checkbox
A -> AMF : click Save

AMF -> DC : connect()
activate DC
DC --> AMF : Connection
deactivate DC
AMF -> MOV : INSERT INTO Movies\n(Title, ReleaseDate, Language, CountryOfOrigin,\n Genre, DirectorId, LeadingActorId,\n SupportingActorId, About, Rating,\n Comments, Poster, ParentalRestriction)\nVALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
activate MOV
MOV --> AMF : 1 row inserted
deactivate MOV
AMF --> A  : JOptionPane "Movie added!"
AMF -> AMF : dispose()
deactivate AMF
AMF -> AF  : [windowClosed] parentFrame.setEnabled(true)
AF  -> AF  : loadMovieCards() [refresh grid]

== Edit Movie ==
A -> AF  : select movie card, click EditMovieButton
AF -> AF : guard: selectedMovieId == -1 → warning
AF -> AF : this.setEnabled(false)
AF -> EMF : new EditMovieForm(selectedMovieId, this)
activate EMF
EMF -> DC : connect()
activate DC
DC --> EMF : Connection
deactivate DC
EMF -> MOV : SELECT m.*, YEAR(m.ReleaseDate),\nCONCAT(d/la/sa names)\nFROM Movies m LEFT JOIN Persons x3\nWHERE m.MovieID = ?
activate MOV
MOV --> EMF : full movie row
deactivate MOV
EMF -> EMF : pre-fill all form fields\nwith current values
EMF -> PER : SELECT persons for combos
activate PER
PER --> EMF : person rows
deactivate PER
EMF -> AMF : Theme.applyToSub(this)
EMF --> A  : EditMovieForm prefilled

A -> EMF : modify fields as needed
A -> EMF : click Save
EMF -> DC : connect()
activate DC
DC --> EMF : Connection
deactivate DC
EMF -> MOV : UPDATE Movies\nSET Title=?, ReleaseDate=?, Language=?,\n CountryOfOrigin=?, Genre=?,\n DirectorId=?, LeadingActorId=?,\n SupportingActorId=?, About=?,\n Rating=?, Comments=?, Poster=?,\n ParentalRestriction=?\nWHERE MovieID=?
activate MOV
MOV --> EMF : 1 row updated
deactivate MOV
EMF --> A  : JOptionPane "Movie updated!"
EMF -> EMF : dispose()
deactivate EMF
EMF -> AF  : [windowClosed] parentFrame.setEnabled(true)
AF  -> AF  : loadMovieCards()

== Delete Movie ==
A -> AF : select movie card, click DeleteMovieButton
AF -> AF : guard: selectedMovieId == -1 → warning
AF -> A  : JOptionPane.showConfirmDialog\n"Delete this movie?"
alt User clicks NO
  A --> AF : no action
else User clicks YES
  AF -> DC : connect()
  activate DC
  DC --> AF : Connection
  deactivate DC
  AF -> MOV : DELETE FROM Movies\nWHERE MovieID = ?
  activate MOV
  MOV --> AF : 1 row deleted
  deactivate MOV
  AF --> A  : JOptionPane "Deleted!"
  AF -> AF  : selectedMovieId = -1
  AF -> AF  : loadMovieCards() [refresh grid]
end

== Toggle Parental Restriction ==
A -> AF : select movie card, click SetRestrictionButton
AF -> AF : guard: selectedMovieId == -1 → warning
AF -> DC : connect()
activate DC
DC --> AF : Connection
deactivate DC
AF -> MOV : UPDATE Movies\nSET ParentalRestriction =\n  NOT ParentalRestriction\nWHERE MovieID = ?
activate MOV
MOV --> AF : 1 row updated
deactivate MOV
AF --> A  : JOptionPane "Restriction changed!"
AF -> AF  : loadMovieCards() [grid refreshes\n — card re-renders RESTRICTED/FAMILY badge]
@enduml
```

---

## 6. Sequence Diagram — Content Moderation

```plantuml
@startuml SEQ_Moderation
skinparam sequenceArrowThickness 2

actor "Admin\n(Adult User)"        as A
participant "AdultFrame"           as AF
participant "ModerationFrame"      as MF
participant "DatabaseConnection"   as DC
database    "UserMovieInteractions" as UMI

A -> AF : click ModerateContentButton
AF -> AF : this.setEnabled(false)
AF -> MF : new ModerationFrame(this)
activate MF
MF -> MF : initComponents()\n[jTable1 with cols:\nInteractionID(hidden), Username,\nMovie Title, Rating, Comment, Status]
MF -> MF : Theme.applyToSub(this)\n[dark palette + custom cell renderers]
MF -> MF : loadInteractions()

MF -> DC : connect()
activate DC
DC --> MF : Connection
deactivate DC
MF -> UMI : SELECT i.InteractionID, u.Username, m.Title,\n i.Rating, i.Comment, i.Status\nFROM UserMovieInteractions i\nJOIN Users u ON i.UserID = u.UserId\nJOIN Movies m ON i.MovieID = m.MovieID\nORDER BY FIELD(i.Status,\n  'pending','approved','rejected')
activate UMI
UMI --> MF : result rows\n[pending rows appear first]
deactivate UMI
MF -> MF : model.setRowCount(0)\nthen model.addRow() for each row
MF --> A  : ModerationFrame visible\n[table populated]

loop Admin reviews each pending item

  A -> MF : select row in jTable1
  MF -> MF : jTable1.getSelectedRow()\n→ interactionId = jTable1.getValueAt(row, 0)

  alt Admin clicks Approve
    A -> MF : click ApproveButton
    MF -> DC : connect()
    activate DC
    DC --> MF : Connection
    deactivate DC
    MF -> UMI : UPDATE UserMovieInteractions\nSET Status = 'approved'\nWHERE InteractionID = ?
    activate UMI
    UMI --> MF : 1 row updated
    deactivate UMI
    MF --> A  : JOptionPane "Status updated to: APPROVED"
    MF -> MF  : loadInteractions() [refresh table]

  else Admin clicks Reject
    A -> MF : click RejectButton
    MF -> DC : connect()
    activate DC
    DC --> MF : Connection
    deactivate DC
    MF -> UMI : UPDATE UserMovieInteractions\nSET Status = 'rejected'\nWHERE InteractionID = ?
    activate UMI
    UMI --> MF : 1 row updated
    deactivate UMI
    MF --> A  : JOptionPane "Status updated to: REJECTED"
    MF -> MF  : loadInteractions()

  else Admin clicks Refresh
    A -> MF : click RefreshButton
    MF -> MF : loadInteractions()
    MF --> A  : table reloaded from DB
  end

end

A -> MF : close window [DISPOSE_ON_CLOSE]
MF -> AF : [windowClosed]\nparentFrame.setEnabled(true)\nparentFrame.toFront()
deactivate MF
@enduml
```

---

## 7. Entity-Relationship Diagram

```plantuml
@startuml ER_MovieCritics
skinparam linetype ortho

entity "Persons" as Persons {
  * PersonID       : INT          <<PK>>
  --
  * FirstName      : VARCHAR(100)
  * LastName       : VARCHAR(100)
    DateOfBirth    : DATE
    Nationality    : VARCHAR(100)
}

entity "Movies" as Movies {
  * MovieID            : INT AUTO_INCREMENT <<PK>>
  --
  * Title              : VARCHAR(255)
    ReleaseDate        : DATE
    Language           : VARCHAR(100)
    CountryOfOrigin    : VARCHAR(100)
    Genre              : VARCHAR(100)
    DirectorId         : INT           <<FK → Persons.PersonID>>
    Watched            : BOOLEAN DEFAULT FALSE
    LeadingActorId     : INT           <<FK → Persons.PersonID>>
    SupportingActorId  : INT           <<FK → Persons.PersonID>>
    About              : TEXT
    Rating             : INT CHECK(1..10)
    Comments           : TEXT
    Poster             : VARCHAR(255)
  * ParentalRestriction: BOOLEAN DEFAULT FALSE
}

entity "Users" as Users {
  * UserId   : INT AUTO_INCREMENT <<PK>>
  --
  * Username : VARCHAR(100) UNIQUE
  * Password : VARCHAR(100)
    UserType : INT   [1=Adult, 2=Child]
    Email    : VARCHAR(255) UNIQUE
}

entity "UserMovieInteractions" as UMI {
  * InteractionID : INT AUTO_INCREMENT <<PK>>
  --
  * UserID  : INT  <<FK → Users.UserId>>
  * MovieID : INT  <<FK → Movies.MovieID>>
    Rating  : INT  CHECK(1..10) NULL
    Comment : TEXT NULL
    Watched : BOOLEAN DEFAULT FALSE
    Watchlist: BOOLEAN DEFAULT FALSE
  * Status  : VARCHAR(20) DEFAULT 'pending'\n           [pending | approved | rejected]
  UNIQUE KEY (UserID, MovieID)
}

' Persons is director of 0..* Movies
Persons ||--o{ Movies : "directs\n(DirectorId)"

' Persons is leading actor in 0..* Movies
Persons ||--o{ Movies : "leads\n(LeadingActorId)"

' Persons is supporting actor in 0..* Movies
Persons ||--o{ Movies : "supports\n(SupportingActorId)"

' 1 User has 0..* Interactions
Users   ||--o{ UMI : "makes\n1 to 0..*"

' 1 Movie has 0..* Interactions
Movies  ||--o{ UMI : "receives\n1 to 0..*"

note bottom of UMI
  Composite unique key (UserID, MovieID)
  means at most one interaction record
  per user per movie.
  Status gates what Child users can see:
  only 'approved' interactions are shown
  in Family Ratings view.
end note

note right of Movies
  ParentalRestriction = TRUE
  → hidden from ChildFrame
  (all queries add WHERE
  ParentalRestriction = FALSE)
end note
@enduml
```

---

## 8. Data Flow Diagram — Level 0 (Context Diagram)

```plantuml
@startuml DFD_L0
skinparam rectangle {
  BackgroundColor #1F1F1F
  BorderColor #E50914
  FontColor #FFFFFF
}
skinparam arrow {
  Color #B3B3B3
  FontColor #B3B3B3
}
skinparam database {
  BackgroundColor #2F2F2F
  BorderColor #B3B3B3
  FontColor #FFFFFF
}

rectangle "Adult User"        as AU #2F2F2F
rectangle "Child User"        as CU #2F2F2F
rectangle "System Admin\n(Adult, manages users)" as SA #2F2F2F

rectangle "0\nMovieCritics\nSystem" as SYS #E50914

database  "MySQL\nMovieCritics DB" as DB

AU  --> SYS : login credentials\nmovie CRUD commands\nratings / comments\nwatchlist / watched flags\nmoderation decisions\nanalytics requests

SYS --> AU  : movie catalogue\nfamily ratings (approved)\nanalytics report\npersonal watchlist\nprogress summary

CU  --> SYS : login credentials\nsearch / filter inputs\nratings / comments (pending)\nwatchlist updates\nprogress queries

SYS --> CU  : family-safe movie catalogue\nfamily ratings (approved)\npersonal watchlist\nprogress summary

SA  --> SYS : user management commands\n(add / delete / reset password)

SYS --> SA  : user list

SYS --> DB  : SQL SELECT / INSERT /\nUPDATE / DELETE

DB  --> SYS : query result sets
@enduml
```

---

## 9. Data Flow Diagram — Level 1 (Process Decomposition)

```plantuml
@startuml DFD_L1
skinparam rectangle {
  BackgroundColor #1F1F1F
  BorderColor #555555
  FontColor #FFFFFF
}
skinparam database {
  BackgroundColor #2F2F2F
  BorderColor #B3B3B3
  FontColor #FFFFFF
}
skinparam arrow {
  Color #B3B3B3
  FontColor #B3B3B3
  FontSize 10
}

' ── External entities ─────────────────────────────────────────────
rectangle "Adult User"  as AU #2F2F2F
rectangle "Child User"  as CU #2F2F2F

' ── Data stores ───────────────────────────────────────────────────
database  "D1 Users"                  as DS_Users
database  "D2 Movies"                 as DS_Movies
database  "D3 Persons"                as DS_Persons
database  "D4 UserMovieInteractions"  as DS_UMI

' ── Processes ─────────────────────────────────────────────────────
rectangle "P1\nAuthenticate\nUser" as P1
rectangle "P2\nBrowse & Search\nMovies" as P2
rectangle "P3\nRecord\nInteraction\n(Upsert)" as P3
rectangle "P4\nManage\nCatalogue\n(Add/Edit/Delete)" as P4
rectangle "P5\nModerate\nContent" as P5
rectangle "P6\nManage\nUsers" as P6
rectangle "P7\nGenerate\nAnalytics /\nReports" as P7

' ── Authentication ────────────────────────────────────────────────
AU --> P1 : username + password
CU --> P1 : username + password
P1 --> DS_Users : SELECT WHERE\nUsername=? AND Password=?
DS_Users --> P1 : {UserId, UserType}
P1 --> AU : session token\n(userId, role=Adult)
P1 --> CU : session token\n(userId, role=Child)

' ── Browse & Search ───────────────────────────────────────────────
AU --> P2 : genre filter,\nsearch keyword
CU --> P2 : genre filter,\nsearch keyword
P2 --> DS_Movies : SELECT m.*\n+ optional WHERE\nParentalRestriction=FALSE [Child]
DS_Movies --> P2 : movie rows
P2 --> DS_Persons : LEFT JOIN\n(Director/Lead/Support names)
DS_Persons --> P2 : person name rows
P2 --> AU : movie card grid\n(all movies)
P2 --> CU : movie card grid\n(family-safe only)

' ── Record Interaction ────────────────────────────────────────────
AU --> P3 : {movieId, field, value}\n[Watched|Rating|Comment|Watchlist]
CU --> P3 : {movieId, field, value}
P3 --> DS_UMI : SELECT InteractionID\nWHERE UserID=? AND MovieID=?
DS_UMI --> P3 : exists? (yes/no)
P3 --> DS_UMI : UPDATE SET field=?,\nStatus='pending'\nWHERE UserID=? AND MovieID=?\n─ or ─\nINSERT (UserID,MovieID,\nfield,Status='pending')
P3 --> AU : confirmation message\n"Saved / pending approval"
P3 --> CU : confirmation message

' ── Manage Catalogue ─────────────────────────────────────────────
AU --> P4 : movie data\n(title, genre, persons,\nposter, restriction flag)
P4 --> DS_Movies : INSERT / UPDATE /\nDELETE Movies
P4 --> DS_Persons : SELECT (for combos)
DS_Persons --> P4 : person list
P4 --> AU : success / error message

' ── Moderation ───────────────────────────────────────────────────
AU --> P5 : approve / reject\ncommand {InteractionID}
P5 --> DS_UMI : SELECT pending\ninteractions (JOIN Users+Movies)
DS_UMI --> P5 : interaction rows
P5 --> DS_UMI : UPDATE Status=\n'approved'|'rejected'
P5 --> AU : updated moderation table

' ── Manage Users ─────────────────────────────────────────────────
AU --> P6 : add/delete/\nreset-password command
P6 --> DS_Users : INSERT / DELETE /\nUPDATE Users
DS_Users --> P6 : user list
P6 --> AU : updated user table

' ── Analytics & Reports ──────────────────────────────────────────
AU --> P7 : request analytics\nor family ratings
P7 --> DS_Movies : COUNT(*) total,\nCOUNT DISTINCT watched,\nAVG(Rating),\nTOP rated (GROUP BY MovieID)
DS_Movies --> P7 : aggregated stats
P7 --> DS_UMI : SELECT approved\nratings + comments\nWHERE MovieID=?
DS_UMI --> P7 : family rating rows
P7 --> AU : analytics dialog\n(totals / avg / top movie\n/ family ratings)
P7 --> CU : family ratings view\n(approved only)
@enduml
```

---

### Rendering these diagrams

Paste any block into [PlantUML Online](https://www.plantuml.com/plantuml/uml/) or import via **Visual Paradigm → Tools → Import PlantUML**.  
All diagrams use only standard PlantUML constructs (`@startuml … @enduml`) with no external libraries.
