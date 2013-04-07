package com.sid.cloudynote.server.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.PutException;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchException;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.SortExpression;
import com.google.appengine.api.search.SortOptions;
import com.google.appengine.api.search.StatusCode;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.service.UserService;
import com.sid.cloudynote.server.DocumentManager;
import com.sid.cloudynote.server.EmailService;
import com.sid.cloudynote.server.PMF;
import com.sid.cloudynote.shared.Group;
import com.sid.cloudynote.shared.NotLoggedInException;
import com.sid.cloudynote.shared.User;

public class UserServiceImpl extends RemoteServiceServlet implements
		UserService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7824317844070590676L;

	@Override
	@SuppressWarnings("unchecked")
	public User getUser(String email) {
		User user = null;
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		Query q = pm.newQuery(User.class);
		q.setFilter("email == emailParam");
		q.declareParameters("String emailParam");
		q.setRange(0, 1);
		List<User> results;
		try {
			Object obj = q.execute(email);
			if (obj != null) {
				results = (List<User>) obj;
				results = new ArrayList<User>(pm.detachCopyAll(results));
				results.size();
				if (!results.isEmpty()) {
					user = results.get(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			q.closeAll();
			pm.close();
		}
		return user;
	}

	@Override
	public void addAccessEntry(final List<String> emails,
			final Map<Key, Integer> access) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		List<User> users = new ArrayList<User>();
		for (String email : emails) {
			users.add(this.getUser(email));
		}
		if (users != null && users.size() != 0) {
			for (User user : users) {
				for (Entry<Key, Integer> entry : access.entrySet()) {
					user.getAccess().put(entry.getKey(), entry.getValue());
				}
			}
			try {
				pm.currentTransaction().begin();
				pm.makePersistentAll(users);
				pm.currentTransaction().commit();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (pm.currentTransaction().isActive()) {
					pm.currentTransaction().rollback();
				}
				pm.close();
			}
		}
	}

	@Override
	public List<User> getFriends(String email) throws NotLoggedInException {
		checkLoggedIn();
		List<User> friends = new ArrayList<User>();
		User user = getUser(email);
		if (user.getFriends() != null) {
			for (String key : user.getFriends().keySet()) {
				User friend = getUser(key);
				friends.add(friend);
			}
		}
		return friends;
	}

	@Override
	public User addFriend(String email) throws NotLoggedInException {
		checkLoggedIn();
		/**
		 * Retrieve the user with specific email. If not exist send a invite
		 * email
		 */
		User user = this.getUser(email);
		if (user == null) {
			this.inviteUser(email);
			return null;
		}

		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		User currentUser = (User) pm.getObjectById(User.class, getUser()
				.getEmail());
		currentUser.getFriends().put(user.getEmail(), new Date());
		pm.makePersistent(currentUser);
		currentUser = pm.detachCopy(currentUser);

		return currentUser;
	}

	private void checkLoggedIn() throws NotLoggedInException {
		if (getUser() == null) {
			throw new NotLoggedInException("Not logged in.");
		}
	}

	private com.google.appengine.api.users.User getUser() {
		com.google.appengine.api.users.UserService userService = UserServiceFactory
				.getUserService();
		return userService.getCurrentUser();
	}

	/**
	 * The method to send a invitation email to the given email address. Sender
	 * is set as the current logged in user
	 */
	@Override
	public void inviteUser(String email) throws NotLoggedInException {
		String msgBody = getUser().getEmail()
				+ " just invited you to use Cloudy Note.\n"
				+ "Please click on the following link:\n"
				+ "<a href='http://cloudy-note.appspot.com/'>http://cloudy-note.appspot.com/</a>";
		EmailService.sendEmail(getUser().getEmail(), email,
				"You're invited to use Cloudy Note", msgBody);
	}

	@Override
	public void addUserToGroups(String email, List<Key> groups)
			throws NotLoggedInException {
		checkLoggedIn();
		User user = getUser(email);
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();

		List<Key> unchangedGroups = user.getGroups();
		List<Key> tempGroups = new ArrayList<Key>();
		tempGroups.addAll(unchangedGroups);

		// get the removed groups
		tempGroups.removeAll(groups);
		for (Key key : tempGroups) {
			Group temp = pm.getObjectById(Group.class, key);
			temp.getMembers().remove(email);
			pm.makePersistent(temp);
		}

		// get the newly added groups
		tempGroups.clear();
		tempGroups.addAll(groups);
		tempGroups.removeAll(unchangedGroups);
		for (Key key : tempGroups) {
			Group temp = pm.getObjectById(Group.class, key);
			if (!temp.getMembers().contains(email)) {
				temp.getMembers().add(email);
				pm.makePersistent(temp);
			}
		}

		user.getGroups().clear();
		user.getGroups().addAll(groups);
		pm.makePersistent(user);
	}

	@Override
	public User modifyUser(User user) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			if (!user.getEmail().equals(getUser().getEmail())) {
				GWT.log("You don't have the access to modify other's profile");
			} else {
				pm.currentTransaction().begin();
				pm.makePersistent(user);
				createDocumentForUser(user);
				user = pm.detachCopy(user);
				pm.currentTransaction().commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pm.currentTransaction().isActive())
				pm.currentTransaction().rollback();
			pm.close();
		}
		return user;
	}

	@Override
	public User modifyUserProfileImage(User user) {
		// removed the previous image blob
		User previous = getUser(user.getEmail());
		BlobKey blobKey;
		if (previous.getProfileImage() != null) {
			blobKey = new BlobKey(previous.getProfileImage());
			BlobstoreService blobstoreService = BlobstoreServiceFactory
					.getBlobstoreService();
			blobstoreService.delete(blobKey);
		}

		// add the new image
		blobKey = new BlobKey(user.getProfileImage());
		ImagesService imagesService = ImagesServiceFactory.getImagesService();
		user.setProfileImageUrl(imagesService.getServingUrl(blobKey));
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		pm.makePersistent(user);
		user = pm.detachCopy(user);
		pm.close();
		return user;
	}

	@Override
	public List<User> findUser(String text, int i) {
		List<User> result = new ArrayList<User>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		User user;
		try {
			user = pm.getObjectById(User.class, text);
			user = pm.detachCopy(user);
			result.add(user);
		} catch (Exception e) {
			result.addAll(searchUser(text, i));
		} finally {
			pm.close();
		}
		return result;
	}

	private void createDocumentForUser(User user) {
		Document document = Document
				.newBuilder()
				.setId(user.getEmail())
				.addField(
						Field.newBuilder().setName("userName")
								.setText(user.getNickname()))
				.addField(
						Field.newBuilder().setName("fullName")
								.setHTML(user.getFullName())).build();
		try {
			IndexSpec indexSpec = IndexSpec.newBuilder().setName("note-index")
					.build();
			SearchServiceFactory.getSearchService().getIndex(indexSpec)
					.put(document);
		} catch (PutException e) {
			if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult()
					.getCode())) {
				DocumentManager.getIndex().put(document);
			}
		}
	}

	private void deleteDocumentForUser(User user) {
		try {
			SearchServiceFactory
					.getSearchService()
					.getIndex(
							IndexSpec.newBuilder().setName("user-index")
									.build()).delete(user.getEmail());
		} catch (RuntimeException e) {
			GWT.log("Failed to delete document");
		}
	}

	public List<User> searchUser(String text, int limit) {
		List<User> result = new ArrayList<User>();
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		String query;
		if ("".equals(text.trim())) {
			return result;
		} else {
			query = "userName:" + text + " OR fullName:" + text;
		}

		try {
			// Query the index.
			Results<ScoredDocument> results = this.findUserDocuments(query,
					limit, Cursor.newBuilder().build());
			User user;
			for (ScoredDocument document : results) {
				user = pm.detachCopy(pm.getObjectById(User.class,
						document.getId()));
				result.add(user);
			}
		} catch (SearchException e) {
			if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult()
					.getCode())) {
			}
		}
		return result;
	}

	public Results<ScoredDocument> findUserDocuments(String queryString,
			int limit, Cursor cursor) {
		try {
			SortOptions sortOptions = SortOptions
					.newBuilder()
					.addSortExpression(
							SortExpression
									.newBuilder()
									.setExpression("userName")
									.setDirection(
											SortExpression.SortDirection.DESCENDING)
									.setDefaultValue("")).setLimit(1000)
					.build();
			QueryOptions options = QueryOptions.newBuilder().setLimit(limit)
					.setFieldsToReturn("userName", "fullName")
					.setSortOptions(sortOptions).setCursor(cursor).build();
			com.google.appengine.api.search.Query query = com.google.appengine.api.search.Query
					.newBuilder().setOptions(options).build(queryString);
			return SearchServiceFactory
					.getSearchService()
					.getIndex(
							IndexSpec.newBuilder().setName("user-index")
									.build()).search(query);
		} catch (SearchException e) {
			return null;
		}
	}
}
