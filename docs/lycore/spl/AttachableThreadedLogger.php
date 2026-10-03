<?php

/*
 * ██╗   ██╗    ██████╗ ██████╗ ██████╗ ███████╗
 * ██║   ██║   ██╔════╝██╔═══██╗██╔══██╗██╔════╝
 * ██║   ██║   ██║     ██║   ██║██████╔╝█████╗
 * ██║   ██║   ██║     ██║   ██║██╔══██╗██╔══╝
 * ╚██████╔╝██╗╚██████╗╚██████╔╝██║  ██║███████╗
 *  ╚═════╝ ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * @Author: U core
 *
 * @Links:
 *  > LY Core
 *  > LY Core Project
*/

abstract class AttachableThreadedLogger extends \ThreadedLogger{

	/** @var \ThreadedLoggerAttachment */
	protected $attachment = null;

	/**
	 * @param ThreadedLoggerAttachment $attachment
	 */
	public function addAttachment(\ThreadedLoggerAttachment $attachment){
		if($this->attachment instanceof \ThreadedLoggerAttachment){
			$this->attachment->addAttachment($attachment);
		}else{
			$this->attachment = $attachment;
		}
	}

	/**
	 * @param ThreadedLoggerAttachment $attachment
	 */
	public function removeAttachment(\ThreadedLoggerAttachment $attachment){
		if($this->attachment instanceof \ThreadedLoggerAttachment){
			if($this->attachment === $attachment){
				$this->attachment = null;
				foreach($attachment->getAttachments() as $attachment){
					$this->addAttachment($attachment);
				}
			}
		}
	}

	public function removeAttachments(){
		if($this->attachment instanceof \ThreadedLoggerAttachment){
			$this->attachment->removeAttachments();
			$this->attachment = null;
		}
	}

	/**
	 * @return \ThreadedLoggerAttachment[]
	 */
	public function getAttachments(){
		$attachments = [];
		if($this->attachment instanceof \ThreadedLoggerAttachment){
			$attachments[] = $this->attachment;
			$attachments += $this->attachment->getAttachments();
		}

		return $attachments;
	}
}