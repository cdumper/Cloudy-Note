/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.sid.cloudynote.server.serviceImpl;

import javax.jdo.PersistenceManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sid.cloudynote.client.model.InfoNote;
import com.sid.cloudynote.client.service.AddNoteService;
import com.sid.cloudynote.server.PMF;

public class AddNoteServiceImpl extends RemoteServiceServlet implements AddNoteService {

	private static final long serialVersionUID = 1L;

	@Override
	public void addNote(String title,String content) {
		// TODO Auto-generated method stub
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			pm.makePersistent(new InfoNote(title,content,null,null));
			pm.currentTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
		}
		pm.close();
	}

	@Override
	public void addInfoNote(InfoNote note) {
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			pm.makePersistent(note);
			pm.currentTransaction().commit();
		} catch (Exception e) {
		} finally {
			pm.close();
		}
	}
}
