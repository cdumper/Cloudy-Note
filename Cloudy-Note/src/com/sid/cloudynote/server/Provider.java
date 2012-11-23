/*
 * Copyright (C) 2011 hou_jiong@163.com, MyWeb Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sid.cloudynote.server;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.sid.cloudynote.client.Note;




public class Provider {

	private static final Provider mInstance = new Provider();

	private Provider() {
	}

	public static Provider getInstance() {
		return mInstance;
	}

	public Map<String, String> AnalyzeContent(String content) {
		Map<String, String> map = new HashMap<String, String>();

		String[] group = content.split("<Group>");
		for (int i = 1; i < group.length; i++) {

			String[] elements = group[i].split("<Content>");

			if (elements == null || elements.length < 2) {
				continue;
			}

			map.put(elements[0], elements[1]);
		}
		return map;
	}

	public PrintWriter addNote(PrintWriter out, String title, String content) {

		System.out.println("Provider addNote()");

		if (title == null && content == null) {
			out.print("ValueNull");
			return out;
		}

		if (title == null) {
			title = " ";
		}
		if (content == null) {
			content = " ";
		}

		System.out.println("title=" + title);
		System.out.println("content=" + content);

		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			Note newNote = new Note(title, content);
			pm.makePersistent(newNote);
			pm.currentTransaction().commit();
			out.print("OK");
		} catch (Exception e) {
			e.printStackTrace();
			out.print("Error");
		} finally {
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
		}
		pm.close();

		return out;
	}

	public PrintWriter GetNote(PrintWriter out) {

		System.out.println("Provider GetNote()");

		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		try {
			pm.currentTransaction().begin();
			Query query = pm.newQuery(Note.class);
			List<Note> execute = (List<Note>) query.execute("root");
			List<Note> NoteList = execute;
			if (NoteList.iterator().hasNext()) {
				for (Note e : NoteList) {
					out.print("<Title>");
					out.print(e.getTitle());
					out.print("<Content>");
					out.print(e.getContent());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.print("Error");
		} finally {
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
		}
		pm.close();

		return out;
	}
}
