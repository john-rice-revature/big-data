package csvreadwrite
import scala.io.StdIn
import scala.util.matching.Regex
import java.io.FileNotFoundException
import com.mongodb.client.model.Filters
import org.mongodb.scala.model.Filters._
import csvreadwrite.DBDriver.collection

class Cli {

    val userInputFormat : Regex = "(\\w+)\\s*(.*)".r

    def printWelcome(): Unit = {
        for (repeat <- 1 to 4) {
            println("Welcome to the CSV Parser / Reader. This CSV Parser/Reader can also connect to MongoDB and store" +
                " your contacts in the database!")
            Thread.sleep(400)
        }
    }

    def printMenuOptions(): Unit = {
        println("**********")
        println("Choose what you'd like to do below:")
        println("upload [csv file] : select .csv file to parse & insert into DB")
        println("add : add contact to DB")
        println("view : view DB contacts")
        println("remove : remove specific contacts or all contacts from DB")
        println("exit : exit the application")
        println("**********")
    }

    def userMenu(): Unit = {

        printWelcome()
        var userMenuLoop = true

        while(userMenuLoop) {
            printMenuOptions()

            /** UPLOAD CSV SECTION */

            //user input: upload CSV file to DB
            StdIn.readLine() match {
                case userInputFormat(cmd, arg) if cmd.contentEquals("upload") =>
                    try {
                        CSVParser.parseCSV(arg)
                    } catch {
                        case fnf : FileNotFoundException => println(s"Failed to find .CSV file '$arg'")
                        case mr : MatchError => println("Some unknown match error occurred. Please run the program again.")
                    }

                /** ADD CONTACT SECTION  */

                //user input: add a contact to DB
                case userInputFormat(cmd, arg) if cmd.equalsIgnoreCase("add") =>
                    println("What is the contact's name?")
                    val nameInput = StdIn.readLine()
                    println("What is the contact's phone number? (enter only numbers, no country code)")
                    val phoneInput = StdIn.readLine()
                    println("Are they friends, family, or co-workers? Enter one.")
                    val groupInput = StdIn.readLine()
                    println("You're adding the '" +nameInput + "' with the phone number of '"
                + phoneInput + "' to the contact group '" +groupInput + "'. Is this correct? Type 'yes' or 'no'.")
                    StdIn.readLine() match {
                        case "yes" => DBDriver.printResults(collection.insertOne(Contact(nameInput, phoneInput, groupInput)))
                            println("Your contact "+nameInput+ " has been successfully added to the database!")
                            println("")
                        case "no" => println("No new contacts were added. Please try again.")
                            println("")
                        case notRecognized => println(s"$notRecognized is an invalid answer! Try again.")
                    }

                /** VIEW CONTACT SECTION */

                //user input: view contacts in DB
                case userInputFormat(cmd, arg) if cmd.equalsIgnoreCase("view") =>
                    println("Would you like to view all your contacts or specific one? Type 'all' or 'one'.")
                    StdIn.readLine() match {
                        case "all" => if (DBDriver.getResults(collection.find()).isEmpty) println("Your contacts DB is empty. Please try " +
                            "Uploading a .csv or add contacts one by one.")
                        else {
                            println("")
                            println("Results: Full contact list displayed")
                            DBDriver.printResults(collection.find())
                        }
                        case "one" => println("Please type the full name of the contact you are looking for.")
                            val searchContactName = StdIn.readLine()
                            println("")
                            println("RESULTS ARE AS FOLLOWS:")
                            DBDriver.printResults(collection.find(equal("name", searchContactName)))
                            println(searchContactName + " was successfully located.")
                            println("")
                        case notRecognized => println(s"$notRecognized is an invalid answer! Try again.")
                    }


                /** REMOVE CONTACT SECTION  */

                //user input: remove one or all contacts from DB
                case userInputFormat(cmd, arg) if cmd.equalsIgnoreCase("remove") =>
                    println("Do you want to delete all contacts or a specific one? Type 'all' or 'one'.")
                    StdIn.readLine() match {
                        case "all" =>
                            println("Are you sure you want to delete all contacts? Type 'yes' or 'no'.")
                            StdIn.readLine() match {
                                case "yes" => DBDriver.printResults(collection.deleteMany(Filters.exists("name")))
                                    println("ALL CONTACTS HAVE BEEN DELETED FROM YOUR DATABASE!")
                                    println("")
                                case "no" => println("No contacts deleted.")
                                    println("")
                                case notRecognized => println(s"$notRecognized is an invalid answer! Try again typing 'yes' or 'no'.")
                            }
                        case "one" => println("What is the name of the contact you'd like to remove?")
                            val delContactName = StdIn.readLine()
                            if (DBDriver.getResults(collection.find(equal("name", delContactName))).isEmpty)
                               println("Could not find this contact in your database")
                                 else {
                                    println("")
                                    DBDriver.printResults(collection.find(equal("name", delContactName)))
                                    println("^^^ FOUND CONTACT ^^^")
                                    println("Is this the contact you want to delete above?")
                            }
                            StdIn.readLine() match {
                                case "yes" => DBDriver.printResults(collection.deleteOne(equal("name", delContactName)))
                                            println("You successfully deleted '" + delContactName + "' from your contacts " +
                                                        "database!")
                                            println("")
                                case "no" => println("No specific contact has been deleted.")
                                            println("")
                                case notRecognized => println(s"$notRecognized is an invalid answer! Try again typing 'yes' or 'no'.")
                                            }
                                    }

                //user input: exit program
                case userInputFormat(cmd, arg) if cmd.equalsIgnoreCase("exit") =>
                    userMenuLoop = false
                case notRecognized => println(s"$notRecognized")
            }
        }

    }
}

//TODO: add exception handling where misspelled words, contacts or non-commands throw an error and asks user to retry.