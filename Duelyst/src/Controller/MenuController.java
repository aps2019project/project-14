package Controller;


import model.Item.Usable;
import model.account.*;
import model.battle.*;
import model.card.*;
import view.EnterGameMessages;
import view.MenuView;
import view.Request;
import view.enums.ErrorType;
import view.enums.RequestType;
import view.enums.StateType;


public class MenuController {
    private static StateType state = StateType.MAIN_MENU; //todo esme in ye chiz dige bashe behtar nist? (svw)
    // todo masalan chi??(zahra)

    private static Account account;
    private static AllAccount allAccount = AllAccount.getInstance();
    private static MenuView menuView = MenuView.getInstance();
    private static Game game;
    private static Match match;

    public static void main() {
        Request request = new Request(state);// mige signUp ya logIn hast
        request.getNewCommand();
        while (state != StateType.END_PROGRAM) {

            if (state == StateType.MAIN_MENU) {
                EnterGameMessages enterGameMessages = EnterGameMessages.getInstance();
                switch (request.getRequestType()) {
                    case MAIN_MENU_SIGN_UP:
                        enterGameMessages.showSignUpGetUserName();
                        request.getNewLine();
                        while (allAccount.userNameHaveBeenExist(request.getCommand())) {
                            enterGameMessages.showSignUpHaveUserName();
                            enterGameMessages.showSignUpGetUserName();
                            request.getNewLine();
                        }
                        String username = request.getCommand();
                        enterGameMessages.showSignUpGetPassword();
                        request.getNewLine();
                        allAccount.createAccount(username, request.getCommand());
                        account = allAccount.getAccountByName(username);
                        state = StateType.ACCOUNT_MENU;
                        break;
                    case MAIN_MENU_LOGIN:
                        enterGameMessages.showLoginGetName();
                        request.getNewLine();
                        String userName = request.getCommand();
                        while (!allAccount.userNameHaveBeenExist(userName)) {
                            enterGameMessages.showLoginHaveNotName();
                            enterGameMessages.showLoginGetName();
                            request.getNewLine();
                            userName = request.getCommand();
                        }
                        enterGameMessages.showLoginGetPassword();
                        request.getNewLine();
                        while (!allAccount.passwordMatcher(userName, request.getCommand())) {
                            enterGameMessages.showLoginGetPassword();
                            request.getNewLine();
                        }
                        allAccount.login(userName, request.getCommand());
                        account = allAccount.getAccountByName(userName);
                        state = StateType.ACCOUNT_MENU;
                        break;
                    case MAIN_MENU_HELP:
                        menuView.helpForMainMenu();
                        break;
                    case MAIN_MENU_LEADER_BOARD:
                        allAccount.showLeaderBoard();
                        break;
                    case MAIN_MENU_SAVE:
                        //todo
                        break;
                }
            }

            if (state == StateType.ACCOUNT_MENU) {
                while (request.getRequestType() != RequestType.MENU_ENTER_EXIT) {
                    switch (request.getRequestType()) {
                        case MENU_ENTER_COLLECTION:
                            state = StateType.COLLECTION;
                            break;
                        case MENU_ENTER_BATTLE:
                            game = new Game();
                            if (!game.checkPlayerDeck(account, 1))
                                state = StateType.ACCOUNT_MENU;
                            state = StateType.SELECT_MODE;
                            break;
                        case MENU_ENTER_HELP:
                            MenuView accountMenu = MenuView.getInstance();
                            accountMenu.helpForAccountMenu();
                            break;
                        case MENU_ENTER_SHOP:
                            state = StateType.SHOP;
                            break;
                        case MENU_ENTER_EXIT:
                            state = StateType.END_PROGRAM;
                            break;
                    }
                    request = new Request(state);
                    request.getNewCommand();
                }
            }

            if (state == StateType.COLLECTION) {//todo id ro check konam ke chiye :)
                Collection collection = account.getCollection();
                String deckName, id;
                Card card;
                Usable item;
                ErrorType error;
                while (request.getRequestType() != RequestType.COLLECTION_EXIT) {
                    switch (request.getRequestType()) {
                        case COLLECTION_HELP:
                            collection.helpOfCollection();
                            break;
                        case COLLECTION_SHOW:
                            collection.showCardsAndItems();
                            break;
                        case COLLECTION_SHOW_ALL_DECKS:
                            collection.showAlldecks();
                            break;
                        case COLLECTION_SHOW_DECK:
                            deckName = request.getDeckName();
                            collection.showThisDeck(deckName);
                            break;
                        case COLLECTION_ADD_CARD_TO_DECK:
                            deckName = request.getDeckName();
                            id = request.getId();
                            card = collection.passCardByCardId(id);
                            if (card != null) {
                                collection.addCardToThisDeck(card, deckName);
                                break;
                            }
                            item = collection.passUsableItemByUsableItemId(id);
                            if (item != null) {
                                collection.addItemToThisDeck(item, deckName);
                                break;
                            } else {
                                error = ErrorType.HAVE_NOT_CARD_IN_COLLECTION;
                                error.printMessage();
                            }
                            break;
                        case COLLECTION_CREATE_DECK:
                            deckName = request.getDeckName();
                            collection.createDeck(deckName);
                            break;
                        case COLLECTION_DELETE_DECK:
                            deckName = request.getDeckName();
                            collection.deleteDeck(deckName);
                            break;
                        case COLLECTION_REMOVE_CARD_FROM_DECK:
                            deckName = request.getDeckName();
                            id = request.getId();
                            card = collection.passCardByCardId(id);
                            if (card != null) {
                                collection.removeCardFromDeck(card, deckName);
                                break;
                            }
                            item = collection.passUsableItemByUsableItemId(id);
                            if (item != null) {
                                collection.removeItemFromDeck(item, deckName);
                                break;
                            } else {
                                error = ErrorType.HAVE_NOT_CARD_IN_COLLECTION;
                                error.printMessage();
                            }
                            break;
                        case COLLECTION_SEARCH_CARD:
                            id = request.getId();
                            if (collection.searchCardName(id))
                                break;
                            if (collection.searchItemName(id))
                                break;
                            error = ErrorType.HAVE_NOT_CARD_IN_COLLECTION;
                            error.printMessage();
                            break;
                        case COLLECTION_SELECT_DECK:
                            deckName = request.getDeckName();
                            collection.selectADeckAsMainDeck(deckName);
                            break;
                        case COLLECTION_VALIDATE_DECK:
                            deckName = request.getDeckName();
                            collection.validateDeck(deckName);
                            break;
                        case COLLECTION_SAVE:
                            break;
                        case COLLECTION_EXIT:
                            state = StateType.ACCOUNT_MENU;
                            break;
                    }


                    request = new Request(state);
                    request.getNewCommand();

                }
            }

            if (state == StateType.SHOP) {
                Shop shop = Shop.getInstance();
                while (request.getRequestType() != RequestType.SHOP_EXIT) {
                    switch (request.getRequestType()) {
                        case SHOP_SHOW_COLLECTION:
                            account.getCollection().showCardsAndItems();
                            break;
                        case SHOP_SEARCH_COLLECTION_CARD:
                            shop.searchCollection(account, request.getId());
                            break;
                        case SHOP_SEARCH_CARD:
                            shop.search(account, request.getId());
                            break;
                        case SHOP_BUY:
                            shop.buy(account, request.getId());
                            break;
                        case SHOP_SELL:
                            shop.sell(account, request.getId());
                            break;
                        case SHOP_SHOW:
                            shop.show();
                            break;
                        case SHOP_HELP:
                            shop.help();

                    }
                    request = new Request(state);
                    request.getNewCommand();
                }
            }
            if (state == StateType.SELECT_MODE) {
                switch (request.getRequestType()) {
                    case MODE_MULTI_PLAYER:
                        int mode = 0;
                        int numberOfFlags = 0;
                        String command;
                        String userName;
                        menuView.showAllAccount();
                        do {
                            menuView.printer("Select user [user name]");
                            request.getNewLine();
                            userName = request.getCommand();
                            if (!allAccount.userNameHaveBeenExist(userName)) {
                                ErrorType error = ErrorType.USER_NAME_NOT_FOUND;
                                menuView.printError(error);
                            }
                        } while (!allAccount.userNameHaveBeenExist(userName));
                        menuView.showModes();
                        do {
                            menuView.printer("Start multiPlayer game [mode] [number of flags] ");
                            request.getNewLine();
                            command = request.getCommand();
                            if (!command.matches("Start multiPlayer game \\d (\\d+)*"))
                                continue;
                            mode = Integer.parseInt(command.split(" ")[3]);
                            if (command.split(" ").length > 4) {
                                numberOfFlags = Integer.parseInt(command.split(" ")[4]);
                            }
                            //todo in chand khat ham duplicate e
                        } while (mode != 0);

                        Account secondPlayerAccount = allAccount.getAccountByName(userName);
                        if (!game.checkPlayerDeck(secondPlayerAccount, 2)) {
                            state = StateType.ACCOUNT_MENU;
                            break;
                        }

                        //baad gofte be andaze pool e taeen shode vali nagofte pool taeen konim :-?
                        match = game.makeNewMultiGame(mode, numberOfFlags);
                        state = StateType.BATTLE;

                        break;
                    case MODE_SINGLE_PLAYER:
                        state = StateType.SINGLE_GAME;
                        break;
                    case MODE_HELP:

                        break;
                }
            }
            if (state == StateType.SINGLE_GAME) {
                switch (request.getRequestType()) {
                    case SINGLE_CUSTOM: {
                        int mode = 0;
                        int numberOfFLags = 0;
                        boolean valid = false;
                        String deckName = null;
                        String command;
                        menuView.showDecksAndModes(account);
                        do {
                            menuView.printer("Enter Start game [deck name] [mode] [number of flags]");
                            request.getNewLine();
                            command = request.getCommand();
                            if (!command.matches("Start game \\w+ \\d+ (\\d)*"))
                                continue;
                            deckName = command.split(" ")[2];
                            mode = Integer.parseInt(command.split(" ")[3]);
                            if (command.split(" ").length > 4)
                                numberOfFLags = Integer.parseInt(command.split(" ")[4]);
                            if (mode != 0 && deckName != null)
                                valid = true;

                        } while (!valid);
                        match = game.makeNewCustomGame(account, deckName, mode, numberOfFLags);
                        if (match == null) {
                            //todo error: inja age deckname e alaki bashe ya valid nabashe match null mishe
                        }
                        state = StateType.BATTLE;
                        break;
                    }
                    case SINGLE_STORY: {
                        menuView.showLevelsForStory();
                        int level = 0;
                        do {
                            menuView.printer("Enter the level");
                            request.getNewLine();
                            if (!request.getCommand().matches("\\d"))
                                continue;
                            level = Integer.parseInt(request.getCommand());

                        } while (level > 0 && level < 4);
                        match = game.makeNewStoryGame(level);
                        state = StateType.BATTLE;
                        break;
                    }
                }
            }

            if (state == StateType.BATTLE) {
                //match darim
            }
            if (state == StateType.GRAVE_YARD) {

            }
            if (state == StateType.SELECT_ITEM) {

            }
            if (state == StateType.SELECT_CARD) {

            }


        }

    }

}
