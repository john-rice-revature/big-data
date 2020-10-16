package csvreadwrite
import scala.io.StdIn
import scala.util.matching.Regex
import java.io.FileNotFoundException
import com.mongodb.client.model.Filters
import org.mongodb.scala.model.Filters._
import csvreadwrite.DBDriver.collection



class Cli {

    val commandArgPattern : Regex = "(\\w+)\\s*(.*)".r

    def printWelcome(): Unit = {
        for (repeat <- 1 to 6) {
            println("Welcome to the CSV Parser / Reader. This CSV Parser/Reader can also connect to MongoDB and store" +
                " your contacts in the database!")
            println("")
            Thread.sleep(900)
        }
    }

    def printMenuOptions(): Unit = {
        println("SELECT AN OPTION FROM BELOW")
        println("")
        println("upload [csv file] : select .csv file to parse & insert into DB")
        println("add : add contact to DB")
        println("view: view DB contacts")
        println("remove : remove specific contacts or all contacts from DB")
        println("exit : exit the application")

       /** FEATURES BELOW THAT HAVE YET TO BE IMPLEMENTED


       */

    }

    def userMenu(): Unit = {

        printWelcome()
        var userMenuLoop = true

        while(userMenuLoop) {
            printMenuOptions()

            //user input: upload CSV file to DB
            StdIn.readLine() match{
                case commandArgPattern(cmd, arg) if cmd.equals("upload") =>
                    try {
                        CSVParser.parseCSV(arg)
                    } catch {
                        case fnf : FileNotFoundException => println(s"Failed to find .CSV file '$arg'")
                    }

                //user input: view contacts in DB
                case commandArgPattern(cmd, arg) if cmd.equalsIgnoreCase("view") =>
                    DBDriver.printResults(collection.find())

                //user input: add a contact to DB
                case commandArgPattern(cmd, arg) if cmd.equalsIgnoreCase("add") => {
                    println("What is the contact's name? ")
                    val nameInput = StdIn.readLine()
                    println("What is the contact's phone number? (enter only numbers) ")
                    val phoneInput = StdIn.readLine()
                    println("Are they friends, family, or co-workers? ")
                    val groupInput = StdIn.readLine()
                    println("You're adding the '" +nameInput + "' with the phone number of '"
                    + phoneInput + "' to the contact group '" +groupInput + "'. Is this correct? Yes or no.")
                    StdIn.readLine() match {
                        case "yes" => DBDriver.printResults(collection.insertOne(Contact(nameInput, phoneInput, groupInput)))
                            println("Your contact "+nameInput+ " has been successfully added to the database!")
                            println("")
                        case "no" => println("No new contacts were added. Please try again.")
                            println("")
                    }

                }

                //user input: remove one or all contacts from DB
                case commandArgPattern(cmd, arg) if cmd.equalsIgnoreCase("remove") => {
                    println("Do you want to delete all contacts or a specific one? Type All or Specific.")
                    StdIn.readLine() match {
                        case "all" =>
                    println("Are you sure you want to delete all contacts? Yes or No.")
                     StdIn.readLine() match {
                        case "yes" => DBDriver.printResults(collection.deleteMany(Filters.exists("name")))
                            println("ALL CONTACTS HAVE BEEN DELETED FROM YOUR DATABASE!")
                            println("")
                        case "no" => println("No contacts deleted.")
                             println("")
                    }
                        case "specific" => println("What is the name of the contact you'd like to remove?")
                            val delContactName = StdIn.readLine()
                            DBDriver.printResults(collection.find(equal("name", delContactName)))
                            println("^^^ FOUND CONTACT ^^^")
                            println("Is this the contact you want to delete above?")
                            StdIn.readLine() match {
                                case "yes" => DBDriver.printResults(collection.deleteOne(equal("name", delContactName)))
                                    println("You successfully deleted '" +delContactName+ "' from your contacts " +
                                        "database!")
                                    println("")
                                case "no" => println("No specific contact has been deleted.")
                                    println("")
                            }

                    }
                }
                //user input: exit program
                case commandArgPattern(cmd, arg) if cmd.equalsIgnoreCase("exit") =>
                    userMenuLoop = false
                case notRecognized => println(s"$notRecognized not a recognized command!")
            }
        }

    }
}

//TODO: add exception handling where misspelled words, contacts or non-commands throw an error and asks user to retry.