library("R.matlab")
library("bmp")
library("e1071")

#�d�ʐ�
channels <- 0

#���ԑ�
timeTotal <- 0

#�����V���[�Y
series <- 400

#��������(<399)
splitLimit <- 0

#�o�͉摜��
outWidth <- 860

#�o�͉摜��
outHeight <- 860

#���̓t�H���_�p�X�imat�j
#inputDir <- '/R/data'
inputDir <- '/R/test'

#�o�̓t�H���_�p�X(jpg)
outputDir <- '/R/graph'

#�t�H���_�ő��݂���mat�t�@�C���𕪊����ďo��
readMatFolder <- function(){
	setwd(inputDir)

	matfile <- dir()
	cnt <- grep("mat",matfile)
	
	#cat("-----------Split Data----------------------------------------\n")
	for ( i in 1:length(cnt)){
		path = matfile[cnt[i]];	
		print(path)
		readMatFile(path)		
	}

	cat(paste("---------------------------------------------------------------\n"))
	if(splitLimit != 0){
		cat(paste("Exported",splitLimit*length(cnt),"files!\n"))
	}
	setwd('/R')
	#source('/R/test01.R')
}

#matLab�t�@�C���������ďo��
readMatFile <- function(path){
	setwd(inputDir)
	
	x <- readMat(path)
	
	#cat("-----------Orgin Data----------------------------------------\n")
	

	#mat�t�@�C���\���o��
	#print(x[[1]])

	# data �s��
	data <- x[[1]]
	channels = nrow(data)
	timeTotal = ncol(data)
	#cat(paste("row*col:",channels,"*",timeTotal,"\n\n"));
	
	#�f�[�^����
	fileAccount=timeTotal/series-1

		#�O���t�o�͗p�s��쐬
		outputData <- list()
		
		#�t�@�C���o��
		setwd(outputDir)
		
		#�����g�`�iX�F���ԁAY�F�U���j�F
		par(mfrow=c(1,1)) 
		#�]���̈�폜
		par(mar = c(2, 2, 0, 1))
		par(oma = c(0, 0, 0, 0)) 
		plot(data[1,],type="l",xlab="",ylab="",axes=FALSE,col=1)
		par(new=T)
		plot(data[2,],type="l",xlab="",ylab="",axes=FALSE,col=2)
		par(new=T)
		plot(data[3,],type="l",xlab="",ylab="",axes=FALSE,col=3)
		par(new=T)
		plot(data[4,],type="l",xlab="",ylab="",axes=FALSE,col=4)
		par(new=T)
		plot(data[5,],type="l",xlab="",ylab="",axes=FALSE,col=6)
		par(new=T)
		plot(data[6,],type="l",xlab="",ylab="",axes=FALSE,col=8)
		par(new=T)
		plot(data[7,],type="l",xlab="",ylab="",axes=FALSE,col=9)
		par(new=T)
		plot(data[8,],type="l",xlab="",ylab="",axes=FALSE,col=10)
		par(new=T)
		plot(data[9,],type="l",xlab="",ylab="",axes=FALSE,col=11)
		par(new=T)
		plot(data[10,],type="l",xlab="",ylab="",axes=FALSE,col=12)
		par(new=T)
		plot(data[11,],type="l",xlab="",ylab="",axes=FALSE,col=13)
		par(new=T)
		plot(data[12,],type="l",xlab="",ylab="",axes=FALSE,col=14)
		par(new=T)
		plot(data[13,],type="l",xlab="",ylab="",axes=FALSE,col=15)
		par(new=T)
		plot(data[14,],type="l",xlab="",ylab="",axes=FALSE,col=16)
		par(new=T)
		plot(data[15,],type="l",xlab="",ylab="",axes=FALSE,col=17)
		par(new=T)
		plot(data[16,],type="l",xlab="",ylab="",axes=FALSE,col=18)
		#�o�̓t�@�C����
		outputFile <- paste("�}__���g�`.bmp",sep="")
		dev.copy(bmp,outputFile,width=outWidth,height=outHeight)
		dev.off()
		
		#���]�g�iX�F���ԁAY�F�d�ʁj�F
		#�d�ʐ��ʐݒ�
		par(mfrow=c(16,1)) 
		#�]���̈�폜
		par(mar = c(0, 0, 0, 0))
		par(oma = c(0, 0, 0, 0)) 
		plot(data[1,],type="l",xlab="",ylab="",axes=FALSE)
		plot(data[2,],type="l",xlab="",ylab="",axes=FALSE)
		plot(data[3,],type="l",xlab="",ylab="",axes=FALSE)
		plot(data[4,],type="l",xlab="",ylab="",axes=FALSE)
		plot(data[5,],type="l",xlab="",ylab="",axes=FALSE)
		plot(data[6,],type="l",xlab="",ylab="",axes=FALSE)
		plot(data[7,],type="l",xlab="",ylab="",axes=FALSE)
		plot(data[8,],type="l",xlab="",ylab="",axes=FALSE)
		plot(data[9,],type="l",xlab="",ylab="",axes=FALSE)
		plot(data[10,],type="l",xlab="",ylab="",axes=FALSE)
		plot(data[11,],type="l",xlab="",ylab="",axes=FALSE)
		plot(data[12,],type="l",xlab="",ylab="",axes=FALSE)
		plot(data[13,],type="l",xlab="",ylab="",axes=FALSE)
		plot(data[14,],type="l",xlab="",ylab="",axes=FALSE)
		plot(data[15,],type="l",xlab="",ylab="",axes=FALSE)
		plot(data[16,],type="l",xlab="",ylab="",axes=FALSE)
		#�o�̓t�@�C����
		outputFile <- paste("�}__�]�g.bmp",sep="")
		dev.copy(bmp,outputFile,width=outWidth,height=outHeight)
		dev.off()
		
		
		#�U��-���ω��Z�iX�F���ԁAY�F�U���j�F
		par(mfrow=c(1,1)) 
		#�]���̈�폜
		par(mar = c(2, 2, 0, 1))
		par(oma = c(2, 2, 2, 2)) 
		outputData <- colMeans(data)
		plot(0:(length(outputData)-1),outputData,type="l",xlab="����",ylab="�U��",cex.axis = 1.8)
		#�o�̓t�@�C����
		outputFile <- paste("�}__�U��-���ω��Z.bmp",sep="")
		dev.copy(bmp,outputFile,width=outWidth,height=outHeight)
		dev.off()
		
		#���g���iX�F���ԁAY�F���g���j�F
		#�������g���g�`�iX�F���ԁAY�F���g���j�F
		par(mfrow=c(1,1)) 
		#�]���̈�폜
		par(mar = c(2, 2, 0, 1))
		par(oma = c(0, 0, 0, 0)) 
		plot(0:(length(fft(data[1,]))-1),fft(data[1,]),type="l",xlab="",ylab="",axes=FALSE,col=1)
		par(new=T)
		plot(0:(length(fft(data[2,]))-1),fft(data[2,]),type="l",xlab="",ylab="",axes=FALSE,col=2)
		par(new=T)
		plot(0:(length(fft(data[3,]))-1),fft(data[3,]),type="l",xlab="",ylab="",axes=FALSE,col=3)
		par(new=T)
		plot(0:(length(fft(data[4,]))-1),fft(data[4,]),type="l",xlab="",ylab="",axes=FALSE,col=4)
		par(new=T)
		plot(0:(length(fft(data[5,]))-1),fft(data[5,]),type="l",xlab="",ylab="",axes=FALSE,col=6)
		par(new=T)
		plot(0:(length(fft(data[6,]))-1),fft(data[6,]),type="l",xlab="",ylab="",axes=FALSE,col=8)
		par(new=T)
		plot(0:(length(fft(data[7,]))-1),fft(data[7,]),type="l",xlab="",ylab="",axes=FALSE,col=9)
		par(new=T)
		plot(0:(length(fft(data[8,]))-1),fft(data[8,]),type="l",xlab="",ylab="",axes=FALSE,col=10)
		par(new=T)
		plot(0:(length(fft(data[9,]))-1),fft(data[9,]),type="l",xlab="",ylab="",axes=FALSE,col=11)
		par(new=T)
		plot(0:(length(fft(data[10,]))-1),fft(data[10,]),type="l",xlab="",ylab="",axes=FALSE,col=12)
		par(new=T)
		plot(0:(length(fft(data[11,]))-1),fft(data[11,]),type="l",xlab="",ylab="",axes=FALSE,col=13)
		par(new=T)
		plot(0:(length(fft(data[12,]))-1),fft(data[12,]),type="l",xlab="",ylab="",axes=FALSE,col=14)
		par(new=T)
		plot(0:(length(fft(data[13,]))-1),fft(data[13,]),type="l",xlab="",ylab="",axes=FALSE,col=15)
		par(new=T)
		plot(0:(length(fft(data[14,]))-1),fft(data[14,]),type="l",xlab="",ylab="",axes=FALSE,col=16)
		par(new=T)
		plot(0:(length(fft(data[15,]))-1),fft(data[15,]),type="l",xlab="",ylab="",axes=FALSE,col=17)
		par(new=T)
		plot(0:(length(fft(data[16,]))-1),fft(data[16,]),type="l",xlab="",ylab="",axes=FALSE,col=18)
		#�o�̓t�@�C����
		outputFile <- paste("�}__�����g���g�`.bmp",sep="")
		dev.copy(bmp,outputFile,width=outWidth,height=outHeight)
		dev.off()
		
		#�����g���g�`�F16 channel�iX�F���ԁAY�F�d�ʁj�F
		#�d�ʐ��ʐݒ�
		par(mfrow=c(16,1)) 
		#�]���̈�폜
		par(mar = c(0, 0, 0, 0))
		par(oma = c(0, 0, 0, 0)) 
		plot(0:(length(fft(data[1,]))-1),fft(data[1,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[2,]))-1),fft(data[2,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[3,]))-1),fft(data[3,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[4,]))-1),fft(data[4,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[5,]))-1),fft(data[5,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[6,]))-1),fft(data[6,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[7,]))-1),fft(data[7,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[8,]))-1),fft(data[8,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[9,]))-1),fft(data[9,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[10,]))-1),fft(data[10,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[11,]))-1),fft(data[11,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[12,]))-1),fft(data[12,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[13,]))-1),fft(data[13,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[14,]))-1),fft(data[14,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[15,]))-1),fft(data[15,]),type="l",xlab="",ylab="",axes=FALSE)
		plot(0:(length(fft(data[16,]))-1),fft(data[16,]),type="l",xlab="",ylab="",axes=FALSE)
		#�o�̓t�@�C����
		outputFile <- paste("�}__���g���g�`_16.bmp",sep="")
		dev.copy(bmp,outputFile,width=outWidth,height=outHeight)
		dev.off()
		
		
		#���g��-���ω��Z�iX�F���ԁAY�F���g���j�F
		par(mfrow=c(1,1)) 
		#�]���̈�폜
		par(mar = c(2, 2, 0, 1))
		par(oma = c(2, 2, 2, 2)) 
		outputData <- colMeans(fft(data))
		plot(0:(length(outputData)-1),outputData,type="l",xlab="����",ylab="���g��",cex.axis = 1.8)
		#�o�̓t�@�C����
		outputFile <- paste("�}__���g��-���ω��Z.bmp",sep="")
		dev.copy(bmp,outputFile,width=outWidth,height=outHeight)
		dev.off()
		
		
		#���̑��̏o��
		#�����n���͖@
		par(mfrow=c(1,1)) 
		#�]���̈�폜
		par(oma = c(0, 0, 0, 0)) 
		par(oma = c(2, 2, 2, 2)) 
		#row1�����i�}���o�p�j
		plot(data[1,],type="l",xlab="����(s)",ylab="�U��",cex.axis = 1.8,col=4)
		#�o�̓t�@�C����
		outputFile <- paste("�}_���n���͖@.bmp",sep="")
		dev.copy(bmp,outputFile,width=outWidth,height=outHeight)
		dev.off()
		
		#�����g����͖@
		par(mfrow=c(1,1)) 
		#�]���̈�폜
		par(oma = c(0, 0, 0, 0)) 
		par(oma = c(2, 2, 2, 2)) 
		#row1�����i�}���o�p�j
		plot(0:(length(fft(data[1,]))-1),fft(data[1,]),type="l",xlab="����(s)",ylab="���g��",cex.axis = 1.8,col=4)
		#�o�̓t�@�C����
		outputFile <- paste("�}_���g����͖@.bmp",sep="")
		dev.copy(bmp,outputFile,width=outWidth,height=outHeight)
		dev.off()
		
		#�����Ԏ��g����͖@
		par(mfrow=c(1,1)) 
		#�]���̈�폜
		par(oma = c(0, 0, 0, 0)) 
		par(oma = c(0, 0, 0, 0)) 
		#row1�����i�}���o�p�j
		stftdata <- stft(data)
		plot(stftdata,xlab="",ylab="",axes=FALSE)
		#�o�̓t�@�C����
		outputFile <- paste("�}_���Ԏ��g����͖@.bmp",sep="",col=4)
		dev.copy(bmp,outputFile,width=outWidth,height=outHeight)
		dev.off()
		
		#�e�t�@�C���o��
		#cat(paste("File",outputFile," OK!\n"))
	
		#���W�o��
		#cat("-----------Output Data-------------\n")
		#str(outputData)
}

#TEST�p�FBMP�t�@�C���Ǎ���
readBMP <- function(){	
	testimg <- read.bmp('/R/output/Dog_1_interictal_segment_0001_001.bmp')
	cat(dim(testimg))
	print(testimg)
}

readMatFolder()