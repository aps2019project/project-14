package controller.server;

import com.gilecode.yagson.YaGson;
import com.gilecode.yagson.YaGsonBuilder;
import controller.RequestEnum;
import controller.Transmitter;
import javafx.scene.Group;
import model.account.Account;
import model.account.AllAccount;
import model.account.FilesType;
import model.account.Shop;
import model.battle.Deck;
import model.battle.Game;
import model.card.Card;
import model.card.CardId;
import model.card.makeFile.MakeNewFile;
import model.item.Usable;
import model.requirment.GeneralLogicMethods;
import view.enums.ErrorType;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class RequestEnumController {
    static ArrayList<Group> groupTexts = new ArrayList<>();
    private static ArrayList<SocketClass> chatPerson = new ArrayList<>();

    public static void main(RequestEnum requestEnum, SocketClass socketClass, Transmitter clientTransmitter) {
        Transmitter transmitter = socketClass.getTransmitter();
        transmitter.transmitterId = clientTransmitter.transmitterId;
        AllAccount allAccount = AllAccount.getInstance();
        Account account = socketClass.getAccount();
        switch (requestEnum) {

            case END_OF_CLIENT:
                socketClass.getClientHandlerServer().stop();
                break;
            case SIGN_UP:
                boolean canSignUp = allAccount.signUp(clientTransmitter.name, clientTransmitter.password);
                if (!canSignUp)
                    transmitter.errorType = ErrorType.USER_NAME_ALREADY_EXIST;
                else {
                    socketClass.setAccount(allAccount.getAccountByName(clientTransmitter.name));
                    account.setAuthToken(AllAccount.getInstance().getAuthToken(account));
                }
                transfer(socketClass);
                break;
            case LOGIN:
                if (!allAccount.userNameHaveBeenExist(clientTransmitter.name))
                    transmitter.errorType = ErrorType.USER_NAME_NOT_FOUND;
                else if (!allAccount.passwordMatcher(clientTransmitter.name, clientTransmitter.password))
                    transmitter.errorType = ErrorType.PASSWORD_DOES_NOT_MATCH;
                else {
                    socketClass.setAccount(allAccount.getAccountByName(clientTransmitter.name));
                    account.setAuthToken(AllAccount.getInstance().getAuthToken(account));
                }
                transfer(socketClass);
                break;
            case LOGOUT:
                AllAccount.getInstance().saveAccount(account);
                account.setAuthToken(null);
                socketClass.setAccount(null);
                break;
            case PROFILE:
                transmitter.path = account.getAccountImagePath();
                transmitter.name = account.getUserName();
                transmitter.matchInfos = account.getMatchHistory();
                transmitter.accounts = AllAccount.getInstance().showLeaderBoard();
                transfer(socketClass);
                break;
            case SHOP_BUY_AND_SELL:
                break;
            case SHOP_BUY:
                transmitter.errorType = Shop.getInstance().buy(account, clientTransmitter.name);
                transmitter.daric = account.getDaric();
                transfer(socketClass);
                break;
            case SHOP_SELL:
                transmitter.errorType = Shop.getInstance().sell(account, clientTransmitter.name);
                transmitter.daric = account.getDaric();
                transfer(socketClass);
                break;
            case SHOP_CARDS:
                transmitter.cards = Shop.getInstance().getCards();
                transfer(socketClass);
                break;
            case SHOP_HELP:
                transmitter.string = Shop.getInstance().help();
                transfer(socketClass);
                break;
            case SHOP_SEARCH:
                transmitter.object = Shop.getInstance().search(account, clientTransmitter.name);
                transmitter.name = account.getUserName();
                transmitter.collection = account.getCollection();
                if(transmitter.object == null)
                {
                    transmitter.errorType = ErrorType.NO_SUCH_CARD_OR_ITEM_IN_SHOP;
                }
                transfer(socketClass);
                break;
            case SHOP_ITEMS:
                ArrayList<Usable> items = Shop.getInstance().getItems();
                transmitter.items = new ArrayList<>();
                transmitter.items.addAll(items);
                transfer(socketClass);
                break;
            case SHOP_DARIC:
                transmitter.daric = account.getDaric();
                transfer(socketClass);
                break;


            case COLLECTION_SHOW:
                transmitter.cards = account.getCollection().getAllCards();
                transmitter.items = new ArrayList<>(Arrays.asList(account.getCollection().getItems()));
                transfer(socketClass);
                break;
            case COLLECTION_DECKS:
                transmitter.decks = account.getDecks();
                transmitter.collection = account.getCollection();
                for (Card card : transmitter.collection.getAllCards())
                    System.out.println(card.getName() + " " + card.getCardId());
                transfer(socketClass);
                break;
            case COLLECTION_NEW_DECK:
                transmitter.errorType = account.getCollection().createDeck(clientTransmitter.name);
                break;
            case COLLECTION_DELETE_DECK:
                transmitter.errorType = account.getCollection().deleteDeck(clientTransmitter.name);
            case COLLECTION_EXPORT:
                Deck deck = clientTransmitter.deck;
                String path = "exportedDeck/" + account.getUserName()
                        + "." + deck.getName() + ".json";
                GeneralLogicMethods.saveInFile(path, deck);
                break;
            case COLLECTION_IMPORT:
                InputStream input;
                try {
                    input = new FileInputStream("exportedDeck/"
                            + account.getUserName() + "." + clientTransmitter.deck.getName() + ".json");
                    Reader reader = new InputStreamReader(input);
                    YaGson mapper = new YaGson();
                    Deck deckImported = mapper.fromJson(reader, Deck.class);//load the deck
                    transmitter.deck = deckImported;
                    if (!account.getCollection().checkTheDeckForImport(deckImported)) {
                        transmitter.errorType =
                                ErrorType.HAVE_NOT_CARDS_IN_COLLECTION_FOR_IMPORT;

                    }
                } catch (FileNotFoundException e) {
                    transmitter.errorType = ErrorType.INVALID_NAME_FOR_IMPORTED_DECK;
                } finally {
                    transfer(socketClass);
                }

                break;
            case COLLECTION_ADD_CARD_TO_DECK:
                //todo
                break;
            case COLLECTION_SELECT_MAIN_DECK:
                transmitter.errorType =
                        account.getCollection().selectADeckAsMainDeck(transmitter.deck.getName());
                break;
            case COLLECTION_CARDS:
                transmitter.cards = account.getCollection().getAllCards();
                transfer(socketClass);
                break;
            case COLLECTION_ITEMS:
                transmitter.items = new ArrayList<>(Arrays.asList(account.getCollection().getItems()));
                transfer(socketClass);
                break;
            case COLLECTION_HELP:
                transmitter.string = account.getCollection().helpOfCollection();
                transfer(socketClass);
                break;
            case COLLECTION_SEARCH_ITEM:
                transmitter.ids = account.getCollection().searchItemName(clientTransmitter.name);
                transfer(socketClass);
                break;
            case COLLECTION_SEARCH_CARD:
                transmitter.ids = account.getCollection().searchCardName(clientTransmitter.name);
                transfer(socketClass);
                break;
            case ENTER_CHAT:
                chatPerson.add(socketClass);
                account = account;
                transmitter.name = account.getUserName();
                transmitter.path = account.getAccountImagePath();
                transfer(socketClass);
                break;
            case NEW_CARD_ID:
                account = account;
                Card card = transmitter.card;
                new CardId(account, card, account.getCollection().getNumberOfCardId(card));
                account.getCollection().addToCards(card);
                break;
            case SEND_MESSAGE:
//                groupTexts.add(group);
                for (SocketClass person : chatPerson) {
                    person.changeTransmitter();
                    Transmitter personTransmitter = person.getTransmitter();
                    personTransmitter.transmitterId = 0;
                    personTransmitter.requestEnum = RequestEnum.NEW_MESSAGE;
                    personTransmitter.profile = clientTransmitter.profile;
                    personTransmitter.name = clientTransmitter.name;
                    personTransmitter.message = clientTransmitter.message;
                    personTransmitter.path = clientTransmitter.path;
                    transfer(person);
                }
                break;
            case DECKS:
                transmitter.decks = account.getDecks();
                transfer(socketClass);
                break;
            case START_STORY_GAME: {
                Game game = new Game();
                socketClass.setGame(game);
                ErrorType errorType = game.checkPlayerDeck(account, clientTransmitter.playerNumber);
                transmitter.errorType = errorType;
                if (errorType == null) {
                    transmitter.match = socketClass.setMatch(game.makeNewStoryGame(clientTransmitter.level));
                    transmitter.game = game;
                }
                transfer(socketClass);
                break;
            }
            case EXIT_FROM_CHAT:
                chatPerson.remove(socketClass);
                break;
            case START_CUSTOM_GAME: { /*Custom Game*/
                Game game = new Game();
                socketClass.setGame(game);
                ErrorType errorType = game.checkPlayerDeck(account, clientTransmitter.playerNumber);
                transmitter.errorType = errorType;
                if (errorType == null) {
                    transmitter.match = socketClass.setMatch(
                            game.makeNewCustomGame(account, clientTransmitter.name,
                                    clientTransmitter.mode, clientTransmitter.numberOfFlag));
                    transmitter.game = game;
                }
                transfer(socketClass);
                break;
            }
            case MAKE_NEW_CARD: {
                MakeNewFile makeNewFile = new MakeNewFile();
                makeNewFile.setSpriteNumber(clientTransmitter.spriteNumber, clientTransmitter.spriteNumberCount);
                makeNewFile.setHashMaps(clientTransmitter.hashMapsWithStrings);
                socketClass.getTransmitter().errorType = makeNewFile.makeNewCard(FilesType.getEnum(clientTransmitter.type));
                transfer(socketClass);
                break;
            }
            case NEW_CARD_ARRAYLISTS:{
                MakeNewFile makeNewFile = new MakeNewFile();
                transmitter.fieldNames = makeNewFile.getFieldNames(FilesType.getEnum(clientTransmitter.type));
                transmitter.changeFieldNames = makeNewFile.getChangeFieldNames();
                transmitter.targetFieldNames = makeNewFile.getTargetFieldNames();
                transmitter.buffFieldNames = makeNewFile.getBuffFieldNames();
                transfer(socketClass);
            }
        }
        socketClass.changeTransmitter();

    }

    private static void transfer(SocketClass socketClass) {
        try {
            YaGson altMapper = new YaGsonBuilder().create();
            String json = altMapper.toJson(socketClass.getTransmitter());
            socketClass.getOut().println(json);
            socketClass.getOut().flush();

            System.out.println("RequestEnumController.transfer");
            //System.out.println("to client " + json);

            /*
            socketClass.getOutputStream().writeObject(socketClass.getTransmitter());
            socketClass.getOutputStream().flush();
            */
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
